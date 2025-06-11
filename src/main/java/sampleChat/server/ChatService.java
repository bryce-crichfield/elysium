package sampleChat.server;

import common.IMessage;
import common.IService;
import common.ServiceContext;
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
