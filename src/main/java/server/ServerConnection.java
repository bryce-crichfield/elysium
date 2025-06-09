package server;

import interfaces.IConnection;
import interfaces.IConnectionHandler;
import interfaces.IMessage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;

public class ServerConnection implements IConnection, Runnable {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String connectionId;
    private IConnectionHandler handler;
    private volatile boolean connected = false;

    public ServerConnection(Socket socket, IConnectionHandler handler) throws Exception {
        this.socket = socket;
        this.handler = handler;
        this.connectionId = UUID.randomUUID().toString();
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            connected = true;

            handler.onConnect(this);

            while (connected && !socket.isClosed()) {
                IMessage message = (IMessage) in.readObject();
                handler.onMessage(this, message);
            }
        } catch (Exception e) {
            if (connected) {
                handler.onError(this, e);
            }
        } finally {
            cleanup();
        }
    }

    @Override
    public void send(IMessage message) throws Exception {
        if (out != null && connected) {
            out.writeObject(message);
            out.flush();
        } else {
            throw new Exception("Connection not available");
        }
    }

    @Override
    public String getId() {
        return connectionId;
    }

    @Override
    public boolean isConnected() {
        return connected && !socket.isClosed();
    }

    @Override
    public void close() throws Exception {
        connected = false;
        cleanup();
    }

    private void cleanup() {
        try {
            connected = false;
            handler.onDisconnect(this);
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (Exception e) {
            handler.onError(this, e);
        }
    }
}
