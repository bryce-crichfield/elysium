package sampleGame.server;

import common.IConnection;
import common.IMessage;
import common.IServices;
import server.AbstractServer;
import server.Services;

public class BattleServer extends AbstractServer {
  private final BattleService battleService = new BattleService();

  public BattleServer() {
    super();

    IServices battleServices = new Services();
    battleServices.register(battleService);

    servicesRegistry.put("BattleServices", battleServices);
  }

  @Override
  protected void handleMessage(IConnection connection, IMessage message) {}

  public static void main(String[] args) {
    BattleServer server = new BattleServer();
    try {
      server.start(12345);
      System.out.println("Server started on port 12345");

      Runtime.getRuntime()
          .addShutdownHook(
              new Thread(
                  () -> {
                    try {
                      server.stop();
                      System.out.println("Server stopped");
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                  }));

      // we send a tick to all clients every 100ms
      while (server.isRunning()) {
        var battleData = server.battleService.getData().deepCopy();
        // battle data is serializable so clone it for broadcast
        //                server.broadcast(new BattleTick(battleData)); // Assuming BattleTick is an
        // IMessage for game updates
        Thread.sleep(100); // 100ms tick rate
      }

      Thread.sleep(Long.MAX_VALUE);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
