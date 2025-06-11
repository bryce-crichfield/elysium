package client.runtime.system.loading;

import client.runtime.config.RuntimeArguments;
import client.runtime.system.System;
import client.runtime.system.SystemContext;
import java.util.List;

public class LoadingSystem extends System {
  private final LoadingThread thread = new LoadingThread();

  public LoadingSystem(SystemContext runtimeContext) {
    super(runtimeContext);
  }

  @Override
  public void activate(RuntimeArguments arguments) throws Exception {
    thread.start();
  }

  @Override
  public void deactivate() throws Exception {
    thread.interrupt();
  }

  public void queueStages(List<LoadingStage> stages) {
    if (thread == null) {
      throw new IllegalStateException("LoadingSystem is not initialized.");
    }
    thread.queueStages(stages);
  }

  public float getProgress() {
    if (thread == null) {
      throw new IllegalStateException("LoadingSystem is not initialized.");
    }
    return thread.getProgress();
  }

  public boolean isComplete() {
    if (thread == null) {
      throw new IllegalStateException("LoadingSystem is not initialized.");
    }
    return thread.isComplete();
  }

  public String getLoadingMessage() {
    if (thread == null) {
      throw new IllegalStateException("LoadingSystem is not initialized.");
    }

    return thread.getLoadingMessage();
  }
}
