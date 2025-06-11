package client.core.scene;

import client.runtime.application.Application;

@FunctionalInterface
public interface ApplicationSceneFactory {
  ApplicationScene create(Application game);
}
