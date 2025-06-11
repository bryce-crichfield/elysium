package client.runtime.system.loading.stages;

import client.runtime.application.Application;
import client.runtime.system.System;
import client.runtime.system.loading.LoadingStage;

public class SystemLoadingStage<T extends System> implements LoadingStage {
  Application application;
  Class<T> systemClass;

  public SystemLoadingStage(Application application, Class<T> systemClass) {
    this.application = application;
    this.systemClass = systemClass;
  }

  @Override
  public String getDescription() {
    return "Initializing system: " + systemClass.getSimpleName();
  }

  @Override
  public void loadBlocking() throws Exception {
    application.getRuntimeContext().loadSystemBlocking(systemClass, application);
  }
}
