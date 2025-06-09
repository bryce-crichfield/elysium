package sampleChat.server;

import interfaces.IMessage;
import interfaces.IService;
import interfaces.ServiceContext;
import sampleChat.base.Chat;
import sampleChat.base.ChatAction;
import sampleChat.base.ChatUpdate;

import java.util.Optional;

public class ChatService implements IService<ChatAction, IMessage> {
    private final Chat chat = new Chat();

    @Override
    public String getName() {
        return "ChatService";
    }

    @Override
    public Optional<IMessage> execute(ServiceContext context, ChatAction action) throws Exception {
        switch (action) {
            case ChatAction.Add add -> {
                chat.addMessage(add.message());
                context.broadcast(new ChatUpdate(chat.getMessages()));
                return Optional.empty();
            }

            default -> throw new Exception("Unknown action");
        }
    }
}
