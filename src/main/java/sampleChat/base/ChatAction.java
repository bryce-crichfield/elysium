package sampleChat.base;

import common.IMessage;

public interface ChatAction extends IMessage {
    record Add(ChatMessage message) implements ChatAction {}
}
