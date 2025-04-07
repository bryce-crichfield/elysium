package game.state;

import game.Game;
import lombok.RequiredArgsConstructor;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.time.Duration;

@RequiredArgsConstructor
public abstract class GameState {
    protected final Game game;

    public void onEnter() {}

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
