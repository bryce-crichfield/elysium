package game.state;

import game.Game;
import game.graphics.background.Background;
import game.graphics.background.BackgroundFactory;
import game.gui.input.GuiMouseManager;
import game.platform.Renderer;
import lombok.RequiredArgsConstructor;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public abstract class GameState {
    protected final Game game;
    private final List<Background> backgrounds = new ArrayList<>();

    public void addBackground(BackgroundFactory factory) {
        Background background = factory.create(Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
        if (background != null) {
            backgrounds.add(background);
        }
    }

    public void onEnter() {
    }

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

    public void onKeyPressed(int keyCode) {
    }

    public void onKeyReleased(int keyCode) {
    }

    public void onKeyTyped(char keyChar) {
    }

    public final void update(Duration delta) {
        for (Background background : backgrounds) {
            background.update(delta);
        }

        onUpdate(delta);
    }

    public final void render(Renderer renderer) {
        // Clear the screen
        renderer.setColor(new Color(0, 0, 30));
        renderer.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        for (Background background : backgrounds) {
            background.render(renderer);
        }

        onRender(renderer);
    }

    public abstract void onUpdate(Duration delta);

    public abstract void onRender(Renderer renderer);

    public void onExit() {
    }
}
