package interfaces;

import interfaces.ServiceResponse;

public class ServiceContext {
    private final IConnection connection;
    private final String requestId;
    private final IServer server;

    public ServiceContext(IConnection connection, String requestId, IServer server) {
        this.connection = connection;
        this.requestId = requestId;
        this.server = server;
    }

    public void respond(IMessage message) {
        ServiceResponse response = new ServiceResponse(requestId, message);

        try {
            connection.send(response);
        } catch (Exception e) {
            // TODO: Handle exception more gracefully
            throw new RuntimeException(e);
        }
    }

    public void broadcast(IMessage message) {
        server.broadcast(message);
    }
}
