package client.runtime.system;

import client.runtime.config.RuntimeArguments;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Systems {
    private final Map<Class<? extends System>, Function<SystemRuntimeContext, System>> factories = new HashMap<>();
    private final Map<Class<? extends System>, System> instances = new HashMap<>();

    // when we define, we provide the class and a factory
    public <T extends System> void define(Class<T> clazz, Function<SystemRuntimeContext, T> factory) {
        factories.put(clazz, (Function<SystemRuntimeContext, System>) factory);
    }

    // Remember, the plugin may not be initialized yet, so it may return null
    // The default is that it is initialized when we start, but some plugins may choose to initialize later
    // eg. NetworkingManager is only needed when we connect to a server
    public <T extends System> T get(Class<T> clazz) {
        return (T) instances.get(clazz);
    }

    // When we start, we provide the context to each factory and build the instance, we then try to initialize each plugin
    public void start(RuntimeArguments arguments, SystemRuntimeContext context) {
        java.lang.System.out.println("RuntimeSystems: Starting plugins...");
        for (var entry : factories.entrySet()) {
            Class<? extends System> clazz = entry.getKey();
            Function<SystemRuntimeContext, System> factory = entry.getValue();
            System instance = factory.apply(context);
            try {
                if (instance == null){
                    java.lang.System.out.println("RuntimeSystems: Plugin factory returned null for " + clazz.getName() + ", skipping initialization.");
                    continue;
                }
                if (!instance.isAutoStart()) {
                    java.lang.System.out.println("RuntimeSystems: Plugin " + clazz.getName() + " is not set to auto-start, skipping initialization.");
                    continue;
                }
                instance.initialize(arguments);
                instance.setInitialized(true);
                instances.put(clazz, instance);         // we only care about the instance if it initialized successfully
            } catch (Exception e) {
                // report the error but continue initializing other plugins (move on)
                java.lang.System.err.println("Failed to initialize plugin: " + clazz.getName());
                e.printStackTrace();
            }
        }
    }

    public void shutdown() {
        for (var instance : instances.values()) {
            try {
                instance.shutdown();
            } catch (Exception e) {
                // report the error but continue shutting down other plugins (move on)
                java.lang.System.err.println("Failed to shutdown plugin: " + instance.getClass().getName());
                e.printStackTrace();
            }
        }
    }

    public <T extends System> boolean isPluginLoaded(Class<T> clazz) {
        // if the instance is null, then it is not loaded
        // if the instance is not null but isInitialized is false, then it is not loaded
        var instance = instances.get(clazz);
        return instance != null && instance.isInitialized();
    }

    public <T extends System> void loadSystemBlocking(Class<T> clazz, RuntimeArguments arguments, SystemRuntimeContext context ) throws Exception{
            var factory  = factories.get(clazz);
            if (factory == null) {
                java.lang.System.err.println("No factory found for plugin: " + clazz.getName());
                return;
            }

            System instance = factory.apply(context);
             try {
                 instance.initialize(arguments);
                 instance.setInitialized(true);
                 instances.put(clazz, instance);         // we only care about the instance if it initialized successfully
             } catch (Exception e) {
                 // report the error but continue initializing other plugins (move on)
                 throw new Exception("Failed to initialize plugin: " + clazz.getName(), e);
             }
    }
}
