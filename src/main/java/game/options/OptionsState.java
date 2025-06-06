package game.options;

import core.GameContext;
import core.graphics.Renderer;
import core.gui.GuiComponent;
import core.gui.container.GuiContainer;
import core.gui.container.GuiTabPane;
import core.gui.control.GuiButton;
import core.gui.control.GuiDropdown;
import core.gui.layout.GuiHorizontalLayout;
import core.gui.layout.GuiNullLayout;
import core.gui.control.GuiLabel;
import core.gui.style.GuiFont;
import core.input.MouseEvent;
import core.state.GameState;
import core.transition.Transitions;
import core.util.Easing;

import java.awt.*;
import java.time.Duration;

public class OptionsState extends GameState {
    private static final Color BUTTON_COLOR = new Color(50, 50, 100, 200);
    private static final Color BUTTON_HOVER_COLOR = new Color(70, 70, 150, 220);
    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 40;
    private final GuiContainer mainContainer;

    public OptionsState(GameContext gameContext) {
        super(gameContext);

        // Create main container
        mainContainer = new GuiContainer(0, 0, GameContext.SCREEN_WIDTH, GameContext.SCREEN_HEIGHT);
        mainContainer.setLayout(new GuiNullLayout());

        // Create title
        GuiLabel titleLabel = new GuiLabel((GameContext.SCREEN_WIDTH - 200) / 2, 50, "OPTIONS");
        var titleFont = new GuiFont(Color.WHITE, "/fonts/ethnocentric", 36);
        var titleStyle = titleLabel.getStyle().withFont(titleFont);
        titleLabel.setStyle(titleStyle);
        mainContainer.addChild(titleLabel);

        // Create tab pane
        GuiTabPane tabPane = new GuiTabPane(
                (GameContext.SCREEN_WIDTH - 700) / 2,
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

        var buttonsRow = createButtonsRow();
//        mainContainer.addChild(buttonsRow);
    }

    private GuiContainer createButtonsRow() {
        var buttonsRow = new GuiContainer(0, 0, 700, 40);
        buttonsRow.setLayout(new GuiHorizontalLayout());

        GuiComponent backBtn = new GuiButton("Back", BUTTON_WIDTH, BUTTON_HEIGHT, () -> {
            this.gameContext.popState(Transitions.fade(Duration.ofMillis(300), Color.BLACK, Easing.easeOut()));
        });

        GuiComponent saveBtn = new GuiButton("Save", BUTTON_WIDTH, BUTTON_HEIGHT, () -> {
            this.gameContext.popState(Transitions.fade(Duration.ofMillis(300), Color.BLACK, Easing.easeOut()));
        });

        buttonsRow.addChild(backBtn);
        buttonsRow.addChild(saveBtn);
        return buttonsRow;
    }

    private GuiContainer createGraphicsTabContent() {
        GuiContainer container = new GuiContainer(0, 0, 700, 360);
        container.setLayout(new GuiNullLayout());

        // Resolution setting
        var resolutionRow = new GuiContainer(0, 0, 700, 40);
        resolutionRow.setLayout(new GuiHorizontalLayout());
        resolutionRow.addChild(new GuiLabel(50, 40, "Resolution:"));
        var resolutionDropdown = new GuiDropdown<String>(0, 0, 200, 20);
        resolutionDropdown.addItem("1920x1080");
        resolutionDropdown.addItem("1280x720");
        resolutionDropdown.addItem("800x600");
        resolutionDropdown.setSelectedItem("1920x1080");
        resolutionRow.addChild(resolutionDropdown);
        container.addChild(resolutionRow);

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


    @Override
    public void onEnter() {
        // Initialize options state
    }

    @Override
    public void onKeyPressed(int keyCode) {
        // Handle keyboard navigation
    }

    @Override
    public void onMouseEvent(MouseEvent event) {
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
        renderer.fillRect(0, 0, GameContext.SCREEN_WIDTH, GameContext.SCREEN_HEIGHT);

        // Render GUI
        mainContainer.render(renderer);
    }
}