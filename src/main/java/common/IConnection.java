package common;

// Represents any connection (client or server-side)
public interface IConnection {
  String getId();

  void send(IMessage message) throws Exception;

  boolean isConnected();

  void close() throws Exception;
}
