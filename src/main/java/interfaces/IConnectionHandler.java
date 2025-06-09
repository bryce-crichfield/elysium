package interfaces;

public interface IConnectionHandler {
    void onConnect(IConnection connection);
    void onDisconnect(IConnection connection);
    void onMessage(IConnection connection, IMessage message);
    void onError(IConnection connection, Exception error);
}
