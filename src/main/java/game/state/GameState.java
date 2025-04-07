package game.state;

import game.Game;
import game.gui.input.GuiMouseManager;
import lombok.RequiredArgsConstructor;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.time.Duration;

@RequiredArgsConstructor
public abstract class GameState {
    protected final Game game;

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

    public abstract void onUpdate(Duration delta);
    public abstract void onRender(Graphics2D graphics);

    public void onExit() {}
}
