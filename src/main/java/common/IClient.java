package common;

import client.runtime.system.networking.NetworkingConfig;

public interface IClient {
  void connect(NetworkingConfig config) throws Exception;

  void disconnect() throws Exception;

  void send(IMessage message) throws Exception;

  boolean isConnected();

  String getId();
}
