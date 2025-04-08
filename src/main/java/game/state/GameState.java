package game.state;

import game.Game;
import game.graphics.background.Background;
import game.graphics.background.BackgroundFactory;
import game.gui.input.GuiMouseManager;
import lombok.RequiredArgsConstructor;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.time.Duration;
import java.util.List;
import java.util.ArrayList;

@RequiredArgsConstructor
public abstract class GameState {
    protected final Game game;
    private final List<Background> backgrounds = new ArrayList<>();

    public void addBackground(BackgroundFactory factory) {
        Background background = factory.create(game.getWidth(), game.getHeight());
        if (background != null) {
            backgrounds.add(background);
        }
    }

    public void onEnter() {}

    public final void dispatchMouseEvent(MouseEvent event) {
        if (GuiMouseManager.dispatchToCapturedComponent(event)) {
            return; // event was handled by the captured component
        }

        // Handle mouse event in this state
        switch (event.getID()) {
            case MouseEvent.MOUSE_MOVED -> onMouseMoved(event);
            case MouseEvent.MOUSE_DRAGGED -> onMouseDragged(event);
            case MouseEvent.MOUSE_CLICKED -> onMouseClicked(event);
            case MouseEvent.MOUSE_PRESSED -> onMousePressed(event);
            case MouseEvent.MOUSE_RELEASED -> onMouseReleased(event);
            case MouseEvent.MOUSE_WHEEL -> onMouseWheelMoved((MouseWheelEvent) event);
        }
    }

    public void onMouseMoved(MouseEvent event) {
    }
    public void onMouseDragged(MouseEvent event) {
    }
    public void onMouseClicked(MouseEvent event) {
    }
    public void onMousePressed(MouseEvent event) {
    }
    public void onMouseReleased(MouseEvent event) {
    }
    public void onMouseWheelMoved(MouseWheelEvent event) {
    }

    public void onKeyPressed(int keyCode) {}
    public void onKeyReleased(int keyCode) {}
    public void onKeyTyped(char keyChar) {}

    public final void update(Duration delta) {
        for (Background background : backgrounds) {
            background.update(delta);
        }

        onUpdate(delta);
    }

    public final void render(Graphics2D graphics) {
        // Clear the screen
        graphics.setColor(new Color(0, 0, 30));
        graphics.fillRect(0, 0, game.getWidth(), game.getHeight());

        for (Background background : backgrounds) {
            background.render(graphics);
        }

        onRender(graphics);
    }

    public abstract void onUpdate(Duration delta);
    public abstract void onRender(Graphics2D graphics);

    public void onExit() {}
}
