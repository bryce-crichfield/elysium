package client.runtime.application;

@FunctionalInterface
public interface ApplicationFactory {
    Application create(ApplicationRuntimeContext context);
}
