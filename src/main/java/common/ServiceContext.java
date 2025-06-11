package common;

public class ServiceContext {
    private final IConnection connection;
    private final String requestId;
    private final IServer server;

    public ServiceContext(IConnection connection, String requestId, IServer server) {
        this.connection = connection;
        this.requestId = requestId;
        this.server = server;
    }

    public void broadcast(IMessage message) {
        server.broadcast(message);
    }
}
