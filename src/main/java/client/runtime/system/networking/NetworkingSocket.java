package client.runtime.system.networking;

import common.IMessage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class NetworkingSocket {
    private final NetworkingSystem system;

    protected Optional<Socket> socket;
    protected Optional<ObjectOutputStream> out;
    protected Optional<ObjectInputStream> in;

    public NetworkingSocket(NetworkingSystem system) {
        this.system = system;

        this.socket = Optional.empty();
        this.out = Optional.empty();
        this.in = Optional.empty();
    }

    public void connect(NetworkingConfig config) throws Exception {
        socket = Optional.of(new Socket(config.getHost(), config.getPort()));

        if (socket.get().isClosed()) {
            throw new Exception("Failed to connect to server at " + config.getHost() + ":" + config.getPort());
        }

        out = Optional.of(new ObjectOutputStream(socket.get().getOutputStream()));
        in = Optional.of(new ObjectInputStream(socket.get().getInputStream()));
    }

//    public final void sendAsync(IMessage message) throws Exception {
//        var isConnected = system.getNetworkingState() == NetworkingState.CONNECTED;
//        if (out.isPresent() && isConnected) {
//            out.get().writeObject(message);
//
//        } else {
//            throw new Exception("Not connected");
//        }
//    }

    public final CompletableFuture<Void> sendAsync(IMessage message) {
        var isConnected = system.getNetworkingState() == NetworkingState.CONNECTED;

        if (!out.isPresent() || !isConnected) {
            return CompletableFuture.failedFuture(new Exception("Not connected"));
        }

        return CompletableFuture.runAsync(() -> {
            try {
                out.get().writeObject(message);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public final void disconnect() throws Exception {
        if (socket.isPresent() && !socket.get().isClosed()) {
            socket.get().close();
        }

        out.ifPresent(o -> {
            try {
                o.close();
            } catch (Exception e) {
                System.err.println("Error closing output stream: " + e.getMessage());
            }
        });

        in.ifPresent(i -> {
            try {
                i.close();
            } catch (Exception e) {
                System.err.println("Error closing input stream: " + e.getMessage());
            }
        });
    }

    public Optional<IMessage> read() {
        var isConnected = system.getNetworkingState() == NetworkingState.CONNECTED;
        if (in.isPresent() && isConnected) {
            try {
                return Optional.of((IMessage) in.get().readObject());
            } catch (Exception e) {
                System.err.println("Error reading message: " + e.getMessage());
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    public boolean hasClosed() {
        return socket.isEmpty() || !socket.get().isConnected() || socket.get().isClosed();
    }
}
