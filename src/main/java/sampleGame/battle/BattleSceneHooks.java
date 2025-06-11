package sampleGame.battle;

import client.core.scene.ApplicationScene;
import client.runtime.application.Application;
import client.runtime.system.networking.NetworkingHooks;
import client.runtime.system.networking.NetworkingSystem;
import common.IMessage;
import sampleGame.data.BattleData;
import sampleGame.server.BattleTick;

public class BattleSceneHooks implements NetworkingHooks {
    public void onMessage(Application application, IMessage message) {
        if (message instanceof BattleTick response) {
            System.out.println("Received BattleTick - hashCode: " + System.identityHashCode(response));

            BattleData battleData = response.getBattleData();
            System.out.println("BattleData from server - hashCode: " + System.identityHashCode(battleData));

            ApplicationScene scene = application.getScene();
            if (!(scene instanceof BattleScene)) {
                System.err.println("BattleSceneHooks: Invalid scene type, expected BattleScene.");
                return;
            }

            BattleScene battleScene = (BattleScene) scene;
            battleScene.setBattleData(battleData);
        }
    }

    @Override
    public void onServiceResponse(Application application, IMessage message) {

    }

    @Override
    public void onConnected(NetworkingSystem system) {

    }

    @Override
    public void onDisconnected(NetworkingSystem networkingSystem) {

    }

    @Override
    public void onConnectionLost(NetworkingSystem networkingSystem) {

    }
}
