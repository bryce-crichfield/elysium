package game.state.title;

import game.Game;
import game.graphics.Renderer;
import game.graphics.background.Background;
import game.gui.GuiComponent;
import game.gui.container.GuiContainer;
import game.gui.container.GuiScrollPanel;
import game.gui.control.GuiDropdown;
import game.gui.control.GuiSlider;
import game.gui.input.GuiHoverHandler;
import game.gui.input.GuiMouseHandler;
import game.gui.layout.GuiNullLayout;
import game.gui.layout.GuiVerticalLayout;
import game.gui.style.GuiBackground;
import game.gui.style.GuiBorder;
import game.gui.control.GuiLabel;
import game.input.MouseEvent;
import game.state.GameState;
import game.state.battle.BattleState;
import game.state.options.OptionsState;
import game.transition.Transitions;
import game.util.Easing;

import java.awt.*;
import java.time.Duration;
import java.util.List;

public class TitleState extends GameState {
    private static final Color BUTTON_COLOR = new Color(50, 50, 100, 200);
    private static final Color BUTTON_HOVER_COLOR = new Color(70, 70, 150, 220);
    private static final int BUTTON_WIDTH = 250;
    private static final int BUTTON_HEIGHT = 50;
    private static final int BUTTON_SPACING = 20;
    GuiComponent mainMenu;

    public TitleState(Game game) {
        super(game);

        addBackground(Background.stars());

        // Main container
        var container = new GuiContainer(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
        container.setLayout(new GuiNullLayout());

        // Calculate center position
        int centerX = Game.SCREEN_WIDTH / 2;
        int startY = Game.SCREEN_HEIGHT / 3;

        // Add title
        container.addChild(createTitleLabel(centerX, startY - 40));

        // Add menu buttons
        int currentY = startY;

        // Continue button
        container.addChild(createMenuButton(centerX - BUTTON_WIDTH / 2, currentY, "Continue", () -> {
            System.out.println("Continue game");
            // Load saved game logic would go here
        }));
        currentY += BUTTON_HEIGHT + BUTTON_SPACING;

        // New Game button
        container.addChild(createMenuButton(centerX - BUTTON_WIDTH / 2, currentY, "New Game", () -> {
            System.out.println("New game started");
            var transition = Transitions.fade(Duration.ofMillis(2000), Color.BLACK, Easing.cubicEaseIn());
            game.pushState(BattleState::new, transition);
        }));
        currentY += BUTTON_HEIGHT + BUTTON_SPACING;

        // Options button
        container.addChild(createMenuButton(centerX - BUTTON_WIDTH / 2, currentY, "Options", () -> {
            System.out.println("Options opened");
            game.pushState(OptionsState::new, Transitions.fade(Duration.ofMillis(300), Color.BLACK, Easing.easeIn()));
        }));
        currentY += BUTTON_HEIGHT + BUTTON_SPACING;

        // Credits button
        container.addChild(createMenuButton(centerX - BUTTON_WIDTH / 2, currentY, "Credits", () -> {
            System.out.println("Credits opened");
//            this.game.pushState(CreditsState::new, Transitions.fade(Duration.ofMillis(300), Color.BLACK, Easing.easeIn()));
        }));
        currentY += BUTTON_HEIGHT + BUTTON_SPACING;

        // Exit button
        container.addChild(createMenuButton(centerX - BUTTON_WIDTH / 2, currentY, "Exit", () -> {
            System.out.println("Exiting game");
            System.exit(0);
        }));

//        container.addChild(createTestScrollPanel( 100, 100));
//        container.addChild(createTestDropdown( 300, 300));
//        container.addChild(createGuiSlider( 100, 500));

        mainMenu = container;
    }

    public GuiComponent createTestDropdown(int x, int y) {
        var dropdown = new GuiDropdown<String>(x, y, 100, 20);
        dropdown.setItems(List.of("Option 1", "Option 2", "Option 3"));
        dropdown.setSelectedItem("Option 1");
        return dropdown;
    }

    public GuiComponent createTestScrollPanel(int x, int y) {
        var scrollPane = new GuiScrollPanel(x, y, 300, 200);
        scrollPane.setLayout(new GuiVerticalLayout());

        for (int i = 0; i < 20; i++) {
            var label = new GuiLabel(10000, 25, "Label " + (i + 1));
            scrollPane.addChild(label);
        }

//        scrollPane.setBorder(new GuiBorder(Color.WHITE, 2));
//        scrollPane.setBackground(new GuiBackground.Fill(Color.BLACK));

        return scrollPane;
    }

    public GuiComponent createGuiSlider(int x, int y) {
        var container = new GuiContainer(x, y, 300, 100);
        container.setLayout(new GuiVerticalLayout());

        for (int i = 0; i < 10; i++) {
            var slider = new GuiSlider(200, 30);
            slider.setMinValue(0);
            slider.setMaxValue(100);
            slider.setValue(50); // Initial value
            slider.setOnValueChanged(newValue -> {
                // Update your game/application state here
            });
            container.addChild(slider);
        }

//        container.setBackground(new GuiBackground.Fill(Color.BLACK));
//        container.setBorder(new GuiBorder(Color.WHITE, 2));

        return container;
    }

    public GuiComponent createMenuButton(int x, int y, String text, Runnable onClick) {
        var button = new GuiComponent(x, y, BUTTON_WIDTH, BUTTON_HEIGHT) {
            private final boolean hovered = false;
            private GuiBorder border = new GuiBorder(Color.WHITE, 2);

            {
                this.addHoverHandler(new GuiHoverHandler() {

                    @Override
                    public void onEnter(MouseEvent event) {
                        border = border.withColor(Color.YELLOW);
                    }

                    @Override
                    public void onExit(MouseEvent event) {
                        border = border.withColor(Color.WHITE);
                    }
                });
            }

            @Override
            protected void onRender(Renderer g) {
                super.onRender(g);
                g.setColor(hovered ? BUTTON_HOVER_COLOR : BUTTON_COLOR);
                g.fillRect(0, 0, getWidth(), getHeight());

                border.render(g, getWidth(), getHeight(), 0);

                g.setColor(Color.WHITE);
                g.setFont("/fonts/neuropol", 18);

                var metrics = g.getFontInfo();
                int textWidth = metrics.getStringWidth(text);
                int textHeight = metrics.getHeight();
                System.out.println("Text width: " + textWidth + ", Text height: " + textHeight);
                g.drawString(text, (getWidth() - textWidth) / 2, (getHeight() + textHeight / 2) / 2);
            }

            @Override
            protected String getComponentName() {
                return "";
            }
        };

        var mouseClick = GuiMouseHandler.onClick(() -> {
            onClick.run();
            game.getAudio().play("future_ui/beep");

        });
        button.addMouseHandler(mouseClick);

//        button.setBorder(new GuiBorder(Color.WHITE, 2));

        return button;
    }

    public GuiComponent createTitleLabel(int x, int y) {
        var titleLabel = new GuiLabel(100, 50, "ECHOES OF ELYSIUM") {
            @Override
            protected void onRender(Renderer g) {
                g.setColor(Color.WHITE);
                g.setFont("/fonts/ethnocentric", 48);

                var metrics = g.getFontInfo();
                int textWidth = metrics.getStringWidth(getText());

                g.drawString(getText(), -textWidth / 2, 0);
            }
        };

        titleLabel.setPosition(x, y);

        return titleLabel;
    }

    @Override
    public void onEnter() {
    }


    @Override
    public void onKeyPressed(int keyCode) {
        // Handle keyboard navigation if needed
    }

    @Override
    public void onMouseEvent(MouseEvent event) {
        mainMenu.processMouseEvent(event);
    }

    @Override
    public void onUpdate(Duration delta) {
    }

    @Override
    public void onRender(Renderer renderer) {
        mainMenu.render(renderer);
    }
}