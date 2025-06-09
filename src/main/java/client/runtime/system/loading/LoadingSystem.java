package client.runtime.system.loading;

import client.runtime.config.RuntimeArguments;
import client.runtime.system.System;
import client.runtime.system.SystemRuntimeContext;

import java.util.List;

public class LoadingSystem extends System {
    private LoadingThread thread;

    public LoadingSystem(SystemRuntimeContext runtimeContext) {
        super(runtimeContext);
    }

    @Override
    public void initialize(RuntimeArguments arguments) throws Exception {
        if (isInitialized()) {
            return; // already initialized
        }

        thread = new LoadingThread();
        thread.start();
        setInitialized(true);

        java.lang.System.out.println("LoadingSystem initialized with thread: " + thread.getName());
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
