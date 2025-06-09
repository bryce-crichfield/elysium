package client.runtime.system.networking;

import client.runtime.application.Application;
import interfaces.IMessage;

public interface NetworkingHooks {
    void onMessage(Application application, IMessage message);

    void onServiceResponse(Application application, IMessage message);

    void onConnected(NetworkingSystem networkingSystem);

    void onDisconnected(NetworkingSystem networkingSystem);

    void onConnectionLost(NetworkingSystem networkingSystem);

    @FunctionalInterface
    public static interface Factory {
        NetworkingHooks create();
    }
}
