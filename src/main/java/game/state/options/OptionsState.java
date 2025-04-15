package game.state.options;

import game.Game;
import game.input.MouseEvent;
import game.gui.GuiComponent;
import game.gui.GuiContainer;
import game.gui.GuiTabPane;
import game.gui.input.GuiMouseHandler;
import game.gui.layout.GuiNullLayout;
import game.gui.style.GuiBackground;
import game.gui.style.GuiLabel;
import game.platform.Renderer;
import game.state.GameState;
import game.transition.Transitions;
import game.util.Easing;

import java.awt.*;
import java.time.Duration;

public class OptionsState extends GameState {
    private static final Color BUTTON_COLOR = new Color(50, 50, 100, 200);
    private static final Color BUTTON_HOVER_COLOR = new Color(70, 70, 150, 220);
    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 40;
    private final GuiContainer mainContainer;

    public OptionsState(Game game) {
        super(game);

        // Create main container
        mainContainer = new GuiContainer(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
        mainContainer.setLayout(new GuiNullLayout());
        mainContainer.setBackground(new GuiBackground.Fill(new Color(0, 0, 30, 150)));

        // Create title
        GuiLabel titleLabel = new GuiLabel((Game.SCREEN_WIDTH - 200) / 2, 50, "OPTIONS");
//        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setFont(new Font("/fonts/arial", Font.BOLD, 36));
        mainContainer.addChild(titleLabel);

        // Create tab pane
        GuiTabPane tabPane = new GuiTabPane(
                (Game.SCREEN_WIDTH - 700) / 2,
                120,
                700,
                400
        );

        // Create Graphics tab content
        GuiContainer graphicsTab = createGraphicsTabContent();
        tabPane.addTab("Graphics", graphicsTab);

        // Create Audio tab content
        GuiContainer audioTab = createAudioTabContent();
        tabPane.addTab("Audio", audioTab);

        // Create Controls tab content
        GuiContainer controlsTab = createControlsTabContent();
        tabPane.addTab("Controls", controlsTab);

        // Create Game tab content
        GuiContainer gameTab = createGameTabContent();
        tabPane.addTab("Game", gameTab);

        mainContainer.addChild(tabPane);

        // Add back and save buttons
        GuiComponent backButton = createButton((Game.SCREEN_WIDTH - 300) / 2, 550, "Back", () -> {
            this.game.popState(Transitions.fade(Duration.ofMillis(300), Color.BLACK, Easing.easeOut()));
        });

        GuiComponent saveButton = createButton((Game.SCREEN_WIDTH + 60) / 2, 550, "Save", () -> {
            System.out.println("Saving options");
            // Save options logic would go here
            this.game.popState(Transitions.fade(Duration.ofMillis(300), Color.BLACK, Easing.easeOut()));
        });

        mainContainer.addChild(backButton);
        mainContainer.addChild(saveButton);
    }

    private GuiContainer createGraphicsTabContent() {
        GuiContainer container = new GuiContainer(0, 0, 700, 360);
        container.setLayout(new GuiNullLayout());

        // Resolution setting
        container.addChild(new GuiLabel(50, 40, "Resolution:"));
        // Add dropdown or buttons for resolution options

        // Fullscreen setting
        container.addChild(new GuiLabel(50, 100, "Fullscreen:"));
        // Add toggle component

        // Graphics quality setting
        container.addChild(new GuiLabel(50, 160, "Graphics Quality:"));
        // Add slider or dropdown

        // VSync setting
        container.addChild(new GuiLabel(50, 220, "VSync:"));
        // Add toggle component

        return container;
    }

    private GuiContainer createAudioTabContent() {
        GuiContainer container = new GuiContainer(0, 0, 700, 360);
        container.setLayout(new GuiNullLayout());

        // Master volume
        container.addChild(new GuiLabel(50, 40, "Master Volume:"));
        // Add slider

        // Music volume
        container.addChild(new GuiLabel(50, 100, "Music Volume:"));
        // Add slider

        // SFX volume
        container.addChild(new GuiLabel(50, 160, "SFX Volume:"));
        // Add slider

        // UI sound setting
        container.addChild(new GuiLabel(50, 220, "UI Sounds:"));
        // Add toggle

        return container;
    }

    private GuiContainer createControlsTabContent() {
        GuiContainer container = new GuiContainer(0, 0, 700, 360);
        container.setLayout(new GuiNullLayout());

        // Key bindings
        container.addChild(new GuiLabel(50, 40, "Movement:"));
        // Add key binding components

        container.addChild(new GuiLabel(50, 100, "Actions:"));
        // Add key binding components

        container.addChild(new GuiLabel(50, 160, "Menu:"));
        // Add key binding components

        container.addChild(new GuiLabel(50, 220, "Mouse Sensitivity:"));
        // Add slider

        return container;
    }

    private GuiContainer createGameTabContent() {
        GuiContainer container = new GuiContainer(0, 0, 700, 360);
        container.setLayout(new GuiNullLayout());

        // Difficulty
        container.addChild(new GuiLabel(50, 40, "Difficulty:"));
        // Add dropdown or buttons

        // Tutorial hints
        container.addChild(new GuiLabel(50, 100, "Tutorial Hints:"));
        // Add toggle

        // Autosave
        container.addChild(new GuiLabel(50, 160, "Autosave:"));
        // Add toggle or frequency selector

        // Language
        container.addChild(new GuiLabel(50, 220, "Language:"));
        // Add dropdown

        return container;
    }

    private GuiComponent createButton(int x, int y, String text, Runnable onClick) {
        var button = new GuiComponent(x, y, BUTTON_WIDTH, BUTTON_HEIGHT) {
            private final boolean hovered = false;

            @Override
            protected void onRender(Renderer g) {
                super.onRender(g);
                g.setColor(hovered ? BUTTON_HOVER_COLOR : BUTTON_COLOR);
                g.fillRect(0, 0, getWidth(), getHeight());

                g.setColor(Color.WHITE);
                Font buttonFont = new Font("/fonts/arial", Font.BOLD, 16);
                g.setFont(buttonFont);

                var metrics = g.getFontInfo();
                int textWidth = metrics.getStringWidth(text);
                int textHeight = metrics.getHeight();

                g.drawString(text, (getWidth() - textWidth) / 2, (getHeight() + textHeight / 2) / 2);
            }
        };

        var mouseClick = GuiMouseHandler.onClick(onClick);
        var mouseHover = new GuiMouseHandler() {
//            @Override
//            public boolean onMouseMoved(int x, int y) {
//                button.hovered = true;
//                return true;
//            }
//
//            @Override
//            public boolean onMouseExited() {
//                button.hovered = false;
//                return true;
//            }
        };

        button.addMouseHandler(mouseClick);
        button.addMouseHandler(mouseHover);
//        button.setBorder(new GuiBorder(Color.WHITE, 2));

        return button;
    }

    @Override
    public void onEnter() {
        // Initialize options state
    }

    @Override
    public void onMouseWheelMoved(MouseEvent.WheelMoved event) {
        mainContainer.processMouseEvent(event);
    }

    @Override
    public void onKeyPressed(int keyCode) {
        // Handle keyboard navigation
    }

    @Override
    public void onMouseClicked(MouseEvent.Clicked event) {
        mainContainer.processMouseEvent(event);
    }

    @Override
    public void onMouseDragged(MouseEvent.Dragged event) {
        mainContainer.processMouseEvent(event);
    }

    @Override
    public void onMousePressed(MouseEvent.Pressed event) {
        mainContainer.processMouseEvent(event);
    }

    @Override
    public void onMouseReleased(MouseEvent.Released event) {
        mainContainer.processMouseEvent(event);
    }

    @Override
    public void onUpdate(Duration delta) {
        // Update logic
    }

    @Override
    public void onRender(Renderer renderer) {
        // Draw background
        renderer.setColor(new Color(0, 0, 30));
        renderer.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        // Render GUI
        mainContainer.render(renderer);
    }
}