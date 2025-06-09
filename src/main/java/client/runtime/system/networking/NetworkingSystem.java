package client.runtime.system.networking;

import client.runtime.config.RuntimeArguments;
import client.runtime.system.System;
import client.runtime.system.SystemRuntimeContext;
import interfaces.*;
import lombok.Setter;
import lombok.experimental.Delegate;
import server.ErrorMessage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class NetworkingSystem extends System implements IClient, IServiceClient, NetworkingHooks {
    // Connection Management
    protected AtomicBoolean isConnected = new AtomicBoolean(false);
    protected Socket socket;
    protected ObjectOutputStream out;
    protected ObjectInputStream in;

    // We will listen for incoming messages on a separate thread
    protected Thread listenerThread;

    // This is how we "extend" ClientServices, but plugging into certain events
    @Delegate
    @Setter
    protected NetworkingHooks hooks;

    // Service Call Management
    private final ConcurrentHashMap<String, CompletableFuture<IMessage>> pendingRequests = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, IServiceCallback> pendingCallbacks = new ConcurrentHashMap<>();

    public NetworkingSystem(SystemRuntimeContext runtimeContext) {
        this(runtimeContext, new DefaultNetworkingHooks());
    }

    public NetworkingSystem(SystemRuntimeContext runtimeContext, NetworkingHooks hooks) {
        super(runtimeContext);
        this.hooks = hooks;

        setAutoStart(false);
    }


    @Override
    public void initialize(RuntimeArguments arguments) throws Exception {
        java.lang.System.out.println("NetworkingSystem: Initializing with arguments: " + arguments);
        String host = arguments.getOrDefault("host", "localhost");
        String port = arguments.getOrDefault("port", "12345");


        this.connect(host, Integer.parseInt(port));

        if (!isConnected()) {
            throw new Exception("NetworkingSystem: Failed to connect to server at " + host + ":" + port);
        }
    }

    @FunctionalInterface
    public interface Factory {
        NetworkingSystem create(NetworkingHooks hooks);
    }

    @Override
    public void connect(String host, int port) throws Exception {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
        isConnected.set(true);

        listenerThread = new Thread(this::listen);
        listenerThread.start();

        onConnected(this);
    }

    @Override
    public final void disconnect() throws Exception {
        isConnected.set(false);
        if (listenerThread != null) {
            listenerThread.interrupt();
        }
        if (in != null) in.close();
        if (out != null) out.close();
        if (socket != null && !socket.isClosed()) socket.close();

        onDisconnected(this);
    }

    @Override
    public final void send(IMessage message) throws Exception {
        if (out != null && isConnected.get()) {
            out.writeObject(message);
            out.flush();
        } else {
            throw new Exception("Not connected");
        }
    }

    @Override
    public final boolean isConnected() {
        return isConnected.get() && socket != null && !socket.isClosed();
    }

    @Override
    public final String getId() {
        // TODO: Ask server for an ID first, then cache it. For now, return we will provide a random ID?
        return UUID.randomUUID().toString();
    }

    protected void listen() {
        try {
            while (isConnected.get() && !socket.isClosed()) {
                IMessage message = (IMessage) in.readObject();
                handleMessage(message);
            }
        } catch (Exception e) {
            if (isConnected.get()) {
                hooks.onConnectionLost(this);
            }
        }
    }

    @Override
    public IMessage callService(String serviceName, IMessage parameters) throws Exception {
        ServiceCall call = new ServiceCall(serviceName, java.util.UUID.randomUUID().toString(), parameters);
        String requestId = call.requestId();

        CompletableFuture<IMessage> future = new CompletableFuture<>();
        pendingRequests.put(requestId, future);

        try {
            send(call);
            return future.get(30, TimeUnit.SECONDS); // 30 second timeout
        } finally {
            pendingRequests.remove(requestId);
        }
    }


    @Override
    public final IMessage callServiceAsync(String serviceName, IMessage parameters, IServiceCallback callback) throws Exception {
        ServiceCall call = new ServiceCall(serviceName, java.util.UUID.randomUUID().toString(), parameters);
        String requestId = call.requestId();

        pendingCallbacks.put(requestId, callback);
        send(call);

        return call; // Return the request for reference
    }

    protected final void handleMessage(IMessage message) {
        if (message instanceof ServiceResponse response) {
            var requestId = response.requestId();
            if (pendingRequests.containsKey(requestId)) {
                pendingRequests.get(requestId).complete((IMessage) response.result());
            } else if (pendingCallbacks.containsKey(requestId)) {
                pendingCallbacks.get(requestId).onSuccess((IMessage) response.result());
                pendingCallbacks.remove(requestId);
            }
        }

        if (message instanceof ErrorMessage error) {
            java.lang.System.err.println("NetworkingSystem: Error received: " + error);
            // TODO: error should include request id if the error is related to a service call
            // TODO: this way we can kill any pending requests or callbacks
        }

        var client = getContext().getClient();
        hooks.onMessage(client, message);
    }
}
