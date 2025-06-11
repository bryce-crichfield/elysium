package server;

import common.IMessage;

public record ErrorMessage(String error) implements IMessage {
}
