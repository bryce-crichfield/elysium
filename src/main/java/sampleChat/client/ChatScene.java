package sampleChat.client;

import client.runtime.application.Application;
import client.core.graphics.Renderer;
import client.core.gui.GuiComponent;
import client.core.gui.container.GuiContainer;
import client.core.gui.control.GuiButton;
import client.core.gui.control.GuiLabel;
import client.core.gui.control.GuiTextField;
import client.core.gui.layout.GuiNullLayout;
import client.core.gui.style.GuiBackground;
import client.core.gui.style.GuiStyle;
import client.core.input.MouseEvent;
import client.core.scene.ApplicationScene;
import client.runtime.application.ApplicationRuntimeContext;
import client.runtime.config.RuntimeArguments;
import client.runtime.system.SystemState;
import client.runtime.system.networking.NetworkingSystem;
import common.IMessage;
import sampleChat.base.Chat;
import sampleChat.base.ChatAction;
import sampleChat.base.ChatMessage;
import sampleChat.base.ChatUpdate;

import java.awt.*;
import java.time.Duration;

public class ChatScene extends ApplicationScene {

    private boolean isNetworkingAvailable = false; // Default to true, will be set false if plugin fails to load

    private final Chat chat;

    private final GuiComponent root;
    private GuiLabel textArea = new GuiLabel(0, 0, "");
    private GuiTextField inputField = new GuiTextField(0, 0, 400, 30);
    private GuiButton sendButton = new GuiButton("Send", 100, 30, this::sendMessage);

    public ChatScene(Application application) {
        super(application);

        this.chat = new Chat();
        this.root = buildGui();
    }

    public GuiComponent buildGui() {
        RuntimeArguments arguments = getApplication().getRuntimeContext().getArguments();
        int screenWidth = Integer.parseInt(arguments.getOrDefault("screenWidth", "" + Application.SCREEN_WIDTH));
        int screenHeight = Integer.parseInt(arguments.getOrDefault("screenHeight", "" + Application.SCREEN_HEIGHT));

        // Container should use actual screen dimensions, not hardcoded values
        GuiContainer container = new GuiContainer(0, 0, screenWidth, screenHeight);
        container.setLayout(new GuiNullLayout());

        // Create Send Button - centered horizontally, positioned in lower portion
        sendButton = new GuiButton("Send Message", 100, 75, () -> {
            sendMessage();
        });
        sendButton.setPosition((screenWidth - sendButton.getWidth()) / 2,
                screenHeight - 150); // Fixed distance from bottom
        container.addChild(sendButton);

        // Create Input Field - centered horizontally, above the button
        inputField = new GuiTextField(0, 0, Math.min(400, screenWidth - 40), 30); // Responsive width
        inputField.setPosition((screenWidth - inputField.getWidth()) / 2,
                screenHeight - 200); // Above the button
        inputField.setStyle(GuiStyle.builder()
                        .background(new GuiBackground.Fill(Color.RED)
                        ).build());
        container.addChild(inputField);

        // Create Text Area - takes up most of the upper screen area
        int textAreaWidth = screenWidth - 40; // 20px margin on each side
        int textAreaHeight = screenHeight - 280; // Leave space for input and button
        textArea = new GuiLabel(textAreaWidth, textAreaHeight, "");
        textArea.setPosition(20, 20); // 20px margin from top-left
        container.addChild(textArea);

        return container;
    }

    private void sendMessage() {
        if (!isNetworkingAvailable) {
            textArea.setText("Error: Networking is not available cannot send message.");
            return; // Don't send if networking is unavailable
        }

        var networking = getApplication().getRuntimeContext().getSystem(NetworkingSystem.class);
        var clientId = networking.getId();
        var message = new ChatMessage(clientId, "Hello from client!");
        var action = new ChatAction.Add(message);
        try {
            networking.callServiceAsync("ChatService", action, response -> {
                System.out.println("ChatScene: Message sent successfully");
            });
        } catch (Exception e) {
            System.err.println("ChatScene: Failed to send message - " + e.getMessage());
        }
    }


    @Override
    public void onEnter() {
        super.onEnter();
        isNetworkingAvailable = false;
        // Ensure the NetworkingPlugin is loaded (is disabled by default, which allows for some scenes to run without it)
        ApplicationRuntimeContext applicationRuntimeContext = getApplication().getRuntimeContext();
        if (!(applicationRuntimeContext.systems.getState(NetworkingSystem.class) == SystemState.ACTIVE)) {
            try {
                getApplication().getRuntimeContext().loadSystemBlocking(NetworkingSystem.class, getApplication());
                isNetworkingAvailable = true; // if load succeeds, we can send messages
            } catch (Exception e) {
                textArea.setText("Error: Networking plugin failed to load.");
            }
        }
    }

    @Override
    public void onUpdate(Duration delta) {    }

    @Override
    public void onRender(Renderer renderer) {
        root.render(renderer);
    }

    @Override
    public void onMouseEvent(MouseEvent event) {
        root.processMouseEvent(event);
    }

    @Override
    public void onKeyPressed(int keyCode) { }

    public void setMessage(IMessage message) {
    if (message instanceof ChatUpdate  chatUpdate) {
            for (var msg : chatUpdate.getMessages()) {
                // Append each message to the text area
                textArea.setText(textArea.getText() + "\n" + msg.getSender() + ": " + msg.getContent());
            }
        } else {
            System.err.println("ChatScene: Invalid message type received.");
        }
    }
}
