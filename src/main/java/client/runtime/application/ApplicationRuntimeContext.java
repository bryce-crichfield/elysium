package client.runtime.application;


import client.runtime.config.RuntimeArguments;
import client.runtime.system.System;
import client.runtime.system.SystemRuntimeContext;
import client.runtime.system.Systems;

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

    public <T extends System> T getSystem(Class<T> clazz) {
        return systems.get(clazz);
    }

    public RuntimeArguments getArguments() {
        return arguments;
    }

    public <T extends System> boolean isSystemLoaded(Class<T> clazz) {
        return systems.isPluginLoaded(clazz);
    }

    public <T extends System> void loadSystemBlocking(Class<T> networkingPluginClass, Application application) throws Exception {
        systems.loadSystemBlocking(networkingPluginClass, arguments, new SystemRuntimeContext(application));
    }
}
