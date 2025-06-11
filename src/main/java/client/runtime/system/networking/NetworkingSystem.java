package client.runtime.system.networking;

import client.runtime.config.RuntimeArguments;
import client.runtime.system.System;
import client.runtime.system.SystemContext;
import client.runtime.system.SystemFlag;
import common.IMessage;
import common.IServiceCallback;
import common.ServiceCall;
import common.Try;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public final class NetworkingSystem extends System {
    private NetworkingState networkingState = NetworkingState.DISCONNECTED;
    private NetworkingQueue queue = new NetworkingQueue();
    private NetworkingSocket socket = new NetworkingSocket(this);
    private NetworkingThread thread = new NetworkingThread(this);


    public NetworkingSystem(SystemContext runtimeContext) {
        super(runtimeContext);

        setSystemFlag(SystemFlag.IS_AUTO_START_DISABLED, true);
    }

    @Override
    public void activate(RuntimeArguments arguments) throws Exception {
        java.lang.System.out.println("NetworkingSystem: Initializing with arguments: " + arguments);
        String host = arguments.getOrDefault("host", "localhost");
        String port = arguments.getOrDefault("port", "12345");

        NetworkingConfig config = new NetworkingConfig(host, Integer.parseInt(port));

        try {
            socket.connect(config);
            networkingState = NetworkingState.CONNECTED;
            thread.start();
            java.lang.System.out.println("NetworkingSystem: Connected to " + host + ":" + port);
        } catch (Exception e) {
            networkingState = NetworkingState.DISCONNECTED;
            throw new Exception("Failed to connect to server at " + host + ":" + port, e);
        }
    }

    @Override
    public void deactivate() throws Exception {
        thread.interrupt();
        socket.disconnect();
    }

    public Try<ServiceCall> callAsync(String serviceName, IMessage parameters, IServiceCallback callback) {
        return thread.callAsync(serviceName, parameters, callback);
    }

    // Flushes the queue to a list and gives it to the caller
    public List<IMessage> getMessages() {
        var messages = new ArrayList<IMessage>();
        while (!queue.isEmpty()) {
            var message = queue.poll();
            if (message == null) break;

            messages.add(message);
        }
        return messages;
    }
}
