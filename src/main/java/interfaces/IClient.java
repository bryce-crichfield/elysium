package interfaces;

public interface IClient {
    void connect(String host, int port) throws Exception;
    void disconnect() throws Exception;
    void send(IMessage message) throws Exception;
    boolean isConnected();

    String getId();
}
