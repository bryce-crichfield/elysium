package server;

import common.*;

import java.io.Serializable;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractServer implements IServer, IConnectionHandler {
    protected ServerSocket serverSocket;
    protected CopyOnWriteArrayList<IConnection> connections = new CopyOnWriteArrayList<>();
    protected Thread listenerThread;
    protected volatile boolean running = false;
    protected IConnectionHandler connectionHandler;
    protected final Map<String, IServices> servicesRegistry = new HashMap<>();

    @Override
    public void start(int port) throws Exception {
        serverSocket = new ServerSocket(port);
        running = true;

        listenerThread = new Thread(() -> {
            try {
                while (running) {
                    ServerConnection connection = new ServerConnection(serverSocket.accept(), this);
                    connections.add(connection);
                    new Thread(connection).start();
                }
            } catch (Exception e) {
                if (running) {
                    System.err.println("Server error: " + e.getMessage());
                }
            }
        });
        listenerThread.start();
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Stopping server...");
        running = false;
        if (listenerThread != null) {
            listenerThread.interrupt();
        }
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        for (IConnection conn : connections) {
            conn.close();
        }
        connections.clear();
    }

    @Override
    public void broadcast(IMessage message) {
        for (IConnection connection : connections) {
            try {
                connection.send(message);
            } catch (Exception e) {
                System.err.println("Error broadcasting to " + connection.getId() + ": " + e.getMessage());
            }
        }
    }

    @Override
    public void setConnectionHandler(IConnectionHandler handler) {
        this.connectionHandler = handler;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    // Default implementations - can be overridden
    @Override
    public void onConnect(IConnection connection) {
        System.out.println("Client connected: " + connection.getId());
        if (connectionHandler != null) {
            connectionHandler.onConnect(connection);
        }
    }

    @Override
    public void onDisconnect(IConnection connection) {
        connections.remove(connection);
        System.out.println("Client disconnected: " + connection.getId());
        if (connectionHandler != null) {
            connectionHandler.onDisconnect(connection);
        }
    }

    @Override
    public void onMessage(IConnection connection, IMessage message) {
        System.out.println("Message from " + connection.getId() + ": " + message);

        if (connectionHandler != null) {
            connectionHandler.onMessage(connection, message);
            return;
        }

        switch (message) {
            case ServiceCall call -> handleServiceCall(connection, call);
            default -> handleMessage(connection, message);
        }
    }

    @Override
    public void onError(IConnection connection, Exception error) {
        System.err.println("Connection error for " + connection.getId() + ": " + error.getMessage());
        if (connectionHandler != null) {
            connectionHandler.onError(connection, error);
        }
    }

    protected IService getService(String serviceName) {
        for (IServices services : servicesRegistry.values()) {
            IService service = services.get(serviceName);
            if (service != null) {
                return service;
            }
        }
        return null;
    }

    // Abstract method for subclasses to implement custom message handling
    protected void handleServiceCall(IConnection connection, ServiceCall call) {
        var context = new ServiceContext(connection, call.requestId(), this);

        try {
            // If the service is not found, send an error response
            var service = getService(call.name());
            if (service == null) {
                var result = Try.failure("Service not found: " + call.name());
                var response = new ServiceResponse(call.requestId(), result);
                connection.send(response);
                return;
            }


            // If the service doesn't return a value, move on
            var response = service.execute(context, call.request());
            if (!response.isPresent()) {
                return;
            }

            // Send the response back to the client
            var result = Try.success((Serializable) response.get());
            var responseMessage = new ServiceResponse(call.requestId(), result);
            connection.send(responseMessage);
        } catch (Exception e) {
            // The service execution failed, send an error response, barring that log
            try {
                var result = Try.failure("Service execution error: " + e.getMessage());
                var response = new ServiceResponse(call.requestId(), result);
                connection.send(response);
            } catch (Exception ex) {
                System.err.println("Failed to send error message: " + ex.getMessage());
            }
        }
    }

    protected abstract void handleMessage(IConnection connection, IMessage message);

}