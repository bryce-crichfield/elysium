package client.runtime.system;

import client.runtime.config.RuntimeArguments;
import lombok.Getter;
import lombok.Setter;

public abstract class System {
    @Getter
    @Setter
    private boolean isAutoStart = true;

    @Getter
    @Setter
    private boolean initialized = false;

    private final SystemRuntimeContext context;

    public System(SystemRuntimeContext runtimeContext) {
        this.context = runtimeContext;
    }

    public abstract void initialize(RuntimeArguments arguments) throws Exception;

    public SystemRuntimeContext getContext() {
        return context;
    }

    public void shutdown() {

    }
}
