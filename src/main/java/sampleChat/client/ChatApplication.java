package sampleChat.client;

import client.runtime.application.Application;
import client.runtime.application.ApplicationFactory;
import client.core.scene.ApplicationSceneFactory;
import client.runtime.RuntimeContainer;
import client.runtime.config.RuntimeArguments;
import client.runtime.system.networking.NetworkingSystem;

public enum ChatApplication {
    ;
    public static void main(String[] args) throws Exception {
        RuntimeArguments arguments = RuntimeArguments.parse(args);

        ApplicationFactory applicationFactory = Application::new;
        ApplicationSceneFactory applicationSceneFactory = ChatScene::new;

        var runtime = new RuntimeContainer(arguments, applicationFactory, applicationSceneFactory);

        runtime.getSystems().define(NetworkingSystem.class, (runtimeContext) -> {
            return new NetworkingSystem(runtimeContext, new ChatHooks());
        });


        runtime.start();
    }
}
