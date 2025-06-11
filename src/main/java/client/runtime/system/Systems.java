package client.runtime.system;

import client.runtime.config.RuntimeArguments;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class Systems {
    private final Map<Class<? extends System>, Function<SystemContext, System>> factories = new HashMap<>();
    private final Map<Class<? extends System>, System> instances = new HashMap<>();

    // when we define, we provide the class and a factory
    public <T extends System> void define(Class<T> clazz, Function<SystemContext, T> factory) {
        factories.put(clazz, (Function<SystemContext, System>) factory);
    }

    // Remember, the plugin may not be initialized yet, so it may return null
    // The default is that it is initialized when we start, but some plugins may choose to initialize later
    // eg. NetworkingManager is only needed when we connect to a server
    public <T extends System> Optional<T> get(Class<T> clazz) {
        if (factories.get(clazz) == null) {
//            java.lang.System.err.println("No factory found for system: " + clazz.getName());
            return Optional.empty();
        }

        if (instances.get(clazz) == null) {
//            java.lang.System.err.println("System " + clazz.getName() + " is not initialized yet.");
            return Optional.empty();
        }

        return Optional.of(clazz.cast(instances.get(clazz)));
    }

    // Creates an instance of each system using the provided factory
    // If set to auto-start, it will activate the system
    // otherwise it will not activate the system until someone calls activate on it
    public void start(RuntimeArguments arguments, SystemContext context) {
        java.lang.System.out.println("RuntimeSystems: Starting plugins...");
        for (var entry : factories.entrySet()) {
            Class<? extends System> clazz = entry.getKey();
            Function<SystemContext, System> factory = entry.getValue();
            System instance = factory.apply(context);
            try {
                if (instance == null) {
                    java.lang.System.out.println("RuntimeSystems: Plugin factory returned null for " + clazz.getName() + ", skipping initialization.");
                    continue;
                }
                if (instance.getSystemFlag(SystemFlag.IS_AUTO_START_DISABLED)) {
                    java.lang.System.out.println("RuntimeSystems: Plugin " + clazz.getName() + " is not set to auto-start, skipping initialization.");
                    continue;
                }
                instance.activate(arguments);
                instance.setSystemState(SystemState.ACTIVE); // set the state to active after successful activation
                instances.put(clazz, instance);         // we only care about the instance if it initialized successfully
            } catch (Exception e) {
                // report the error but continue initializing other plugins (move on)
                java.lang.System.err.println("Failed to initialize plugin: " + clazz.getName());
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        for (var instance : instances.values()) {
            try {
                instance.deactivate();
            } catch (Exception e) {
                // report the error but continue shutting down other plugins (move on)
                java.lang.System.err.println("Failed to shutdown plugin: " + instance.getClass().getName());
                e.printStackTrace();
            }
        }
    }



    // This will block the caller until the system has yielded control
    // 1. Instantiates the system using the factory
    // 2. Activates the system with the provided arguments
    public <T extends System> void loadSystem(Class<T> clazz, RuntimeArguments arguments, SystemContext context) throws Exception {
        var factory = factories.get(clazz);
        if (factory == null) {
            java.lang.System.err.println("No factory found for system: " + clazz.getName());
            return;
        }

        System instance = factory.apply(context);
        try {
            instance.activate(arguments);
            instance.setSystemState(SystemState.INACTIVE);
            instances.put(clazz, instance);         // we only care about the instance if it initialized successfully
        } catch (Exception e) {
            // report the error but continue initializing other plugins (move on)
            throw new Exception("Failed to activated system: " + clazz.getName(), e);
        }
    }
}
