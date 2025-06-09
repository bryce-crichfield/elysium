package sampleChat.base;

import interfaces.IMessage;

public interface ChatAction extends IMessage {
    record Add(ChatMessage message) implements ChatAction {}
}
