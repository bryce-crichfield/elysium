package server;

import interfaces.IMessage;

public record ErrorMessage(String error) implements IMessage {
}
