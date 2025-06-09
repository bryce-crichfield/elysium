package client.runtime.system.networking;

import client.runtime.application.Application;
import interfaces.IMessage;

public class DefaultNetworkingHooks implements NetworkingHooks {

    @Override
    public void onMessage(Application application, IMessage message) {

    }

    @Override
    public void onServiceResponse(Application application, IMessage message) {

    }

    @Override
    public void onConnected(NetworkingSystem networkingSystem) {

    }

    @Override
    public void onDisconnected(NetworkingSystem networkingSystem) {

    }

    @Override
    public void onConnectionLost(NetworkingSystem networkingSystem) {

    }
}
