package sampleChat.server;

import common.IConnection;
import common.IMessage;
import common.IServices;
import server.AbstractServer;
import server.Services;

public class ChatServer extends AbstractServer {

    public ChatServer() {
        super();

        IServices chatServices = new Services();
        chatServices.register(new ChatService());

        servicesRegistry.put("ChatServices", chatServices);
    }

    @Override
    protected void handleMessage(IConnection connection, IMessage message) {

    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        try {
            server.start(12345);
            System.out.println("RPC server started on port 12345");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    server.stop();
                    System.out.println("Server stopped");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));

            Thread.sleep(Long.MAX_VALUE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
