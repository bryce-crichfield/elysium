package game.state.title;

import game.Game;
import game.gui.GuiComponent;
import game.gui.GuiContainer;
import game.gui.GuiScrollPanel;
import game.gui.input.GuiMouseHandler;
import game.gui.layout.GuiNullLayout;
import game.gui.style.GuiBackground;
import game.gui.style.GuiBorder;
import game.state.GameState;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.time.Duration;

public class MainMenuState extends GameState {
    StarBackground starBackground;
    GuiComponent test;
    float scrollPercentage = 0.0f;


    public GuiComponent createTestBox(int x, int y, int width, int height, String text, Color color) {
        var box = new GuiComponent(x, y, width, height) {
            @Override
            protected void onRender(Graphics2D g) {
                super.onRender(g);
                g.setColor(color);
                g.fillRect(0, 0, width, height);
                g.setColor(Color.BLACK);

                FontMetrics metrics = g.getFontMetrics();
                int textHeight = metrics.getHeight();


                g.drawString(text, 0, height - textHeight / 2);
            }
        };

        var mouseClick = GuiMouseHandler.onClick(() -> {
            System.out.println("Clicked: " + text);
        });
        box.addMouseHandler(mouseClick);

        return box;
    }

    public GuiComponent createTestScroll(int x, int y){
        var container = new GuiScrollPanel(x, y, 300, 300);
        for (int i = 0; i < 25; i++) {
            Color color = i % 2 == 0 ? Color.RED : Color.GREEN;
            container.addChild(createTestBox(10, i * 30, 280, 25, "Test: " + (i + 1), color));
        }
        container.scrollToBottom();
        var mouseHandler = GuiMouseHandler.onClick(() -> {
            System.out.println("Container clicked");
        });
        container.setBackground(new GuiBackground.Fill(Color.LIGHT_GRAY));
        container.setBorder(new GuiBorder(Color.WHITE, 2));
        container.addMouseHandler(mouseHandler);
        return container;
    }

    public MainMenuState(Game game) {
        super(game);
        starBackground = new StarBackground(this, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
        var container = new GuiContainer(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
        container.setLayout(new GuiNullLayout());

        container.addChild(createTestScroll(50, 50));
        container.addChild(createTestScroll(400, 50));

        test = container;
    }

    @Override
    public void onEnter() {
    }

    @Override
    public void onMouseWheelMoved(MouseWheelEvent event) {
        test.processMouseEvent(event);
    }

    @Override
    public void onKeyPressed(int keyCode) {

    }

    @Override
    public void onMouseClicked(MouseEvent event) {
        test.processMouseEvent(event);
    }

    @Override
    public void onMouseDragged(MouseEvent event) {
        test.processMouseEvent(event);
    }

    @Override
    public void onMousePressed(MouseEvent event) {
        test.processMouseEvent(event);
    }

    @Override
    public void onMouseReleased(MouseEvent event) {
        test.processMouseEvent(event);
    }

    @Override
    public void onUpdate(Duration delta) {
        starBackground.onUpdate(delta);
//        test.onUpdate(delta);
    }

    @Override
    public void onRender(Graphics2D graphics) {
        starBackground.onRender(graphics);
        test.render(graphics);
//        gui.render(graphics);
    }
}
