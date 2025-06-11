package sampleChat.base;

import java.util.ArrayList;
import java.util.List;

public class Chat {
  private final List<ChatMessage> messages = new ArrayList<>();

  public void addMessage(ChatMessage message) {
    messages.add(message);
  }

  public List<ChatMessage> getMessages() {
    return new ArrayList<>(messages);
  }
}
