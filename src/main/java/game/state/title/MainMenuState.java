package game.state.title;

import game.Game;
import game.gui.GuiElement;
import game.gui.control.GuiButton;
import game.gui.input.GuiMouseHandler;
import game.gui.layout.GuiVerticalLayout;
import game.gui.scroll.GuiScrollManager;
import game.gui.style.GuiBackground;
import game.state.GameState;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.time.Duration;

public class MainMenuState extends GameState {
    StarBackground starBackground;
    GuiElement test;
    float scrollPercentage = 0.0f;
    public MainMenuState(Game game) {
        super(game);
        starBackground = new StarBackground(this, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

//        TextField textField = new TextField(0, 0, 100, 20);
//        TextField text2 = new TextField(0, 0, 100, 20);
//        Button btn = new Button(0, 0, 50, 20, "Click Me Again Again and Again");
//        btn.setOnClick(() -> {
//        });

        test = new GuiElement(300,300 , 100, 75);
        test.setBackground(new GuiBackground.SolidColor(Color.BLUE));
        test.setGuiMouseHandler(GuiMouseHandler.onClick(() -> {
            System.out.println("Clicked on test element");
        }));
        var layout = new GuiVerticalLayout();
        layout.setSpacing(5);
        layout.setPadding(5);
        test.setLayout(layout);

        var child = new GuiElement(10, 10, 50, 25);
        child.setBackground(new GuiBackground.SolidColor(Color.RED));
        child.setGuiMouseHandler(GuiMouseHandler.onClick(() -> {
            System.out.println("Clicked on child element");

        }));
        test.addChild(child);

        var child2 = new GuiButton(10, 10, 50, 25);
        child2.setBackground(new GuiBackground.SolidColor(Color.GRAY));
        child2.setText("Click me");
        child2.onClick(() -> System.out.println("Clicked on button element"));

        test.addChild(child2);

        var child3 = new GuiElement(10, 10, 50, 25);
        child3.setBackground(new GuiBackground.SolidColor(Color.YELLOW));
        test.addChild(child3);

        var child4 = new GuiElement(10, 10, 50, 25);
        child4.setBackground(new GuiBackground.SolidColor(Color.ORANGE));
        test.addChild(child4);

        var child5 = new GuiElement(10, 10, 50, 25);
        child5.setBackground(new GuiBackground.SolidColor(Color.CYAN));
        test.addChild(child5);

        var scrollManager = new GuiScrollManager();
        test.setScrollManager(scrollManager);
    }

    @Override
    public void onEnter() {
    }

    @Override
    public void onMouseWheelMoved(MouseWheelEvent event) {
        test.onMouseWheelMoved(event);
    }

    @Override
    public void onKeyPressed(int keyCode) {
        // up and down on scroll
        if (keyCode == KeyEvent.VK_UP) {
            scrollPercentage -= 0.1f;
            scrollPercentage = Math.max(0.0f, scrollPercentage);
            test.getScrollManager().scrollToPercentage(scrollPercentage);
        } else if (keyCode == KeyEvent.VK_DOWN) {
            scrollPercentage += 0.1f;
            scrollPercentage = Math.min(1.0f, scrollPercentage);
            test.getScrollManager().scrollToPercentage(scrollPercentage);
        }
    }

    @Override
    public void onMouseClicked(MouseEvent event) {
        test.onMouseClicked(event);
    }

    @Override
    public void onMouseMoved(MouseEvent event) {
    }

    @Override
    public void onUpdate(Duration delta) {
        starBackground.onUpdate(delta);
        test.onUpdate(delta);
    }

    @Override
    public void onRender(Graphics2D graphics) {
        starBackground.onRender(graphics);
        test.onRender(graphics);
//        gui.render(graphics);
    }
}
