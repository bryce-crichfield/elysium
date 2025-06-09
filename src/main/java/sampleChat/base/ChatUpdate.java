package sampleChat.base;

import interfaces.IMessage;

import java.util.List;

public class ChatUpdate implements IMessage {
    private List<ChatMessage> messages;

    public ChatUpdate(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }
}
