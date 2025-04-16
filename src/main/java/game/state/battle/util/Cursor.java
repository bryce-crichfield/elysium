package game.state.battle.util;

import game.Game;
import game.graphics.Renderer;
import game.input.KeyEvent;
import game.input.Keyboard;
import game.input.MouseEvent;
import game.state.battle.BattleState;
import game.util.Util;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.time.Duration;

public class Cursor {
    private final Game game;
    private final BattleState state;

    private final float timerMax = .75f;
    @Getter
    public int cursorX;
    @Getter
    public int cursorY;
    float velocityX;
    float velocityY;
    float accelerationX;
    float accelerationY;
    Mode mode = Mode.NORMAL;
    @Setter
    Color color = Color.RED;
    float timer = 0;

    public Cursor(Game game, BattleState state) {
        this.game = game;
        this.state = state;

        cursorX = 0;
        cursorY = 0;
        velocityX = 0;
        velocityY = 0;
        accelerationX = 0;
        accelerationY = 0;
    }

    private void onCursorMoved() {
        cursorX = Util.clamp(cursorX, 0, state.getScene().getWidth() - 1);
        cursorY = Util.clamp(cursorY, 0, state.getScene().getHeight() - 1);
        game.getAudio().play("type_preview/swipe");
        state.getController().onCursorMoved(this);
    }

    public void setPosition(int x, int y) {
        cursorX = x;
        cursorY = y;
        onCursorMoved();
    }

    public void onKeyPressed(Integer keyCode) {
        switch (keyCode) {
            case Keyboard.LEFT -> {
                cursorX--;
                onCursorMoved();
            }
            case Keyboard.RIGHT -> {
                cursorX++;
                onCursorMoved();
            }
            case Keyboard.UP -> {
                cursorY--;
                onCursorMoved();
            }
            case Keyboard.DOWN -> {
                cursorY++;
                onCursorMoved();

            }
            case KeyEvent.VK_MINUS -> {
                float zoom = state.getCamera().getZoom();
                zoom = Math.max(zoom - 0.25f, 0.25f);
                state.getCamera().setZoom(zoom);
                onCursorMoved();
            }
            case KeyEvent.VK_EQUALS -> {
                float zoom = state.getCamera().getZoom();
                zoom = Math.min(zoom + 0.25f, 2);
                state.getCamera().setZoom(zoom);
                onCursorMoved();
            }
        }
    }

    public void enterBlinkingMode() {
        mode = Mode.BLINKING;
    }

    public void onUpdate(Duration duration) {
        updateCameraKinematics(duration);
    }

    private void updateCameraKinematics(Duration duration) {
        float dt = Util.perSecond(duration);

        if (mode == Mode.BLINKING) {
            timer += dt;
            timer = Util.wrap(timer, 0, timerMax);
        }

        int cursorWorldX = cursorX * Game.TILE_SIZE;
        int cursorWorldY = cursorY * Game.TILE_SIZE;

        velocityX = (cursorWorldX - state.getCamera().getX()) * 10;
        velocityY = (cursorWorldY - state.getCamera().getY()) * 10;

        var cameraX = state.getCamera().getX() + (velocityX * dt);
        var cameraY = state.getCamera().getY() + (velocityY * dt);
        state.getCamera().setX(cameraX);
        state.getCamera().setY(cameraY);

        velocityX *= 0.9;
        velocityY *= 0.9;
    }

    public void onRender(Renderer renderer) {
        int offset = 0;
        if ((mode == Mode.BLINKING || mode == Mode.DILATED) && timer < timerMax / 2) {
            offset = 5;
        }

        int size = Game.TILE_SIZE + offset;
        int x = cursorX * Game.TILE_SIZE - offset / 2;
        int y = cursorY * Game.TILE_SIZE - offset / 2;

        var oldStroke = renderer.getLineWidth();
        renderer.setColor(Color.BLACK);
        renderer.setLineWidth(3);
        renderer.drawRect(x, y, size, size);
        renderer.setLineWidth(oldStroke);
        renderer.setColor(color);
        renderer.drawRect(x, y, size, size);
    }

    public void onMouseClicked(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();

        cursorX = (x / Game.TILE_SIZE);
        cursorY = (y / Game.TILE_SIZE);
        onCursorMoved();
    }

    public void onMouseWheelMoved(MouseEvent.WheelMoved event) {
        int wheelRotation = (int) event.getWheelRotation();
        float newZoom = state.getCamera().getZoom() + (Math.signum(wheelRotation) * -0.1f);
        newZoom = Util.clamp(newZoom, 0.25f, 4f);
        state.getCamera().setZoom(newZoom);
    }

    enum Mode {
        BLINKING,
        DILATED,
        NORMAL
    }
}
