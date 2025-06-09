package client.runtime.application;

@FunctionalInterface
public interface ApplicationFactory {
    public Application create(ApplicationRuntimeContext context);
}
