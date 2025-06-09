package sampleChat.base;

import interfaces.IMessage;

import java.io.Serializable;

public class ChatMessage implements IMessage {
    private final String sender;
    private final String content;

    public ChatMessage(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }
}
