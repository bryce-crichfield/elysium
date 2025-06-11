package client.runtime.system.networking;

import common.*;
import server.ErrorMessage;

import java.util.concurrent.ConcurrentHashMap;

public class NetworkingThread extends Thread {
    private final NetworkingSystem system;
    private final ConcurrentHashMap<String, IServiceCallback> pendingCallbacks = new ConcurrentHashMap<>();
    private volatile boolean running = true;

    public NetworkingThread(NetworkingSystem system) {
        this.system = system;
        setName("NetworkingThread");
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            while (running && system.getNetworkingState() == NetworkingState.CONNECTED) {
                if (system.getSocket().hasClosed()) {
                    system.setNetworkingState(NetworkingState.DISCONNECTED);
                    break;
                }

                var message = system.getSocket().read();
                if (message.isEmpty()) {
                    continue;
                }

                processMessage(message.get());
            }
        } catch (Exception e) {
            System.err.println("NetworkingThread error: " + e.getMessage());
            system.setNetworkingState(NetworkingState.DISCONNECTED);
        } finally {
            cleanup();
        }
    }

    private void processMessage(IMessage message) {
        switch (message) {
            case ServiceResponse response -> {
                String requestId = response.requestId();
                IServiceCallback callback = pendingCallbacks.remove(requestId);

                if (callback != null) {
                    system.getQueue().addServiceResponse(() -> callback.onResponse(response));
                } else {
                    System.err.println("No callback found for response ID: " + requestId);
                }
            }
            case ErrorMessage error -> {
                System.err.println("Error received: " + error);
                // Add error to queue for client handling
                system.getQueue().add(error);
            }
            default -> {
                // Add other message types to queue
                system.getQueue().add(message);
            }
        }
    }

    public Try<ServiceCall> callAsync(String serviceName, IMessage parameters, IServiceCallback callback) {
        ServiceCall call = new ServiceCall(serviceName, java.util.UUID.randomUUID().toString(), parameters);

        pendingCallbacks.put(call.requestId(), callback);

        try {
            system.getSocket().sendAsync(call);
            return Try.success(call);
        } catch (Exception e) {
            pendingCallbacks.remove(call.requestId());
            return Try.failure("Failed to send service call: " + e.getMessage());
        }
    }

    public void shutdown() {
        running = false;
        interrupt();
    }

    private void cleanup() {
        // Notify remaining callbacks of disconnection
        pendingCallbacks.values().forEach(callback -> {
            try {
//                callback.onResponse("Connection lost");
            } catch (Exception e) {
                System.err.println("Error notifying callback: " + e.getMessage());
            }
        });
        pendingCallbacks.clear();
    }
}