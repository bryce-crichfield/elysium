package common;

public interface IServer {
  void start(int port) throws Exception;

  void stop() throws Exception;

  void broadcast(IMessage message);

  void setConnectionHandler(IConnectionHandler handler);

  boolean isRunning();
}
