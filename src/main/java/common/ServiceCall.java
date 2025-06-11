package common;

public record ServiceCall(String name, String requestId, IMessage request) implements IMessage {}
