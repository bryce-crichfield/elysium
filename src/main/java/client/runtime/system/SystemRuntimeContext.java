package client.runtime.system;

import client.runtime.application.Application;

public class SystemRuntimeContext {
    private final Application application;

    public SystemRuntimeContext(Application application) {
        this.application = application;
    }

    public Application getClient() {
        return application;
    }

}
