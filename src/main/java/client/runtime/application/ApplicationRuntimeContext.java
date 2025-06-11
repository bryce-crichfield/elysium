package client.runtime.application;


import client.runtime.config.RuntimeArguments;
import client.runtime.system.System;
import client.runtime.system.SystemContext;
import client.runtime.system.Systems;

import java.util.Optional;

public class ApplicationRuntimeContext {
    private final RuntimeArguments arguments;
    private final Systems systems;

    public ApplicationRuntimeContext(Systems systems, RuntimeArguments arguments) {
        this.systems = systems;
        this.arguments = arguments;
    }

    public Systems getSystems() {
        return systems;
    }

    public <T extends System> Optional<T> getSystem(Class<T> clazz) {
        return systems.get(clazz);
    }

    public RuntimeArguments getArguments() {
        return arguments;
    }

    public <T extends System> void loadSystemBlocking(Class<T> networkingPluginClass, Application application) throws Exception {
        systems.loadSystem(networkingPluginClass, arguments, new SystemContext(application));
    }
}
