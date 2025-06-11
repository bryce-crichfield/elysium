package sampleGame;

import client.core.scene.ApplicationSceneFactory;
import client.runtime.RuntimeContainer;
import client.runtime.application.Application;
import client.runtime.application.ApplicationFactory;
import client.runtime.config.RuntimeArguments;
import client.runtime.system.loading.LoadingSystem;
import client.runtime.system.networking.NetworkingSystem;
import sampleGame.loading.LoadingScene;

public enum Main2 {
  ;

  public enum Main {
    ;

    public static void main(String[] args) throws Exception {
      RuntimeArguments arguments = RuntimeArguments.parse(args);

      ApplicationFactory applicationFactory = Application::new;
      ApplicationSceneFactory applicationSceneFactory = LoadingScene::new;

      RuntimeContainer runtimeContainer =
          new RuntimeContainer(arguments, applicationFactory, applicationSceneFactory);
      runtimeContainer.getSystems().define(LoadingSystem.class, LoadingSystem::new);
      runtimeContainer.getSystems().define(NetworkingSystem.class, NetworkingSystem::new);
      runtimeContainer.start();
    }
  }
}
