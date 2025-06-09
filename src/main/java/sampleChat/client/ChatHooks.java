package sampleChat.client;

import client.core.scene.ApplicationScene;
import client.runtime.application.Application;
import client.runtime.system.networking.NetworkingHooks;
import client.runtime.system.networking.NetworkingSystem;
import interfaces.IMessage;

public class ChatHooks implements NetworkingHooks {
    @Override
    public void onMessage(Application application, IMessage message) {
        ApplicationScene scene = application.getScene();
        if (scene == null || !(scene instanceof ChatScene)) {
            System.err.println("ChatHooks: Invalid scene type, expected ChatScene.");
            return;
        }

        ChatScene chatScene = (ChatScene) scene;
        chatScene.setMessage(message);
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
