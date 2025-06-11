package client.runtime.system;

import client.runtime.application.Application;

public class SystemContext {
    private final Application application;

    public SystemContext(Application application) {
        this.application = application;
    }

    public Application getClient() {
        return application;
    }

}
