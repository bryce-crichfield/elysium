package game.state.battle.util;

import game.Game;
import game.graphics.Renderer;
import game.input.KeyEvent;
import game.input.Keyboard;
import game.input.MouseEvent;
import game.state.battle.event.CursorMoved;
import game.state.battle.world.World;
import game.util.Util;

import java.awt.*;
import java.time.Duration;

public class Cursor {
    private final Game game;
    private final float timerMax = .75f;
    public int cursorX;
    public int cursorY;
    float velocityX;
    float velocityY;
    float accelerationX;
    float accelerationY;
    Camera camera;
    int tileSize;
    Mode mode = Mode.NORMAL;
    Color color = Color.RED;
    float timer = 0;

    public Cursor(Camera camera, Game game, World world) {
        this.camera = camera;
        this.tileSize = Game.TILE_SIZE;
        this.game = game;

        cursorX = 0;
        cursorY = 0;
        velocityX = 0;
        velocityY = 0;
        accelerationX = 0;
        accelerationY = 0;


        // TODO: Should this be in the constructor?
        CursorMoved.event.addListener(event -> {
            cursorX = Util.clamp(cursorX, 0, world.getWidth() - 1);
            cursorY = Util.clamp(cursorY, 0, world.getHeight() - 1);
            game.getAudio().play("type_preview/swipe");
        });
    }

    public void setPosition(int x, int y) {
        cursorX = x;
        cursorY = y;
        CursorMoved.event.fire(this);
    }

    public void onKeyPressed(Integer keyCode) {
        switch (keyCode) {
            case Keyboard.LEFT -> {
                cursorX--;
                CursorMoved.event.fire(this);
            }
            case Keyboard.RIGHT -> {
                cursorX++;
                CursorMoved.event.fire(this);
            }
            case Keyboard.UP -> {
                cursorY--;
                CursorMoved.event.fire(this);
            }
            case Keyboard.DOWN -> {
                cursorY++;
                CursorMoved.event.fire(this);

            }
            case KeyEvent.VK_MINUS -> {
                float zoom = camera.getZoom();
                zoom = Math.max(zoom - 0.25f, 0.25f);
                camera.setZoom(zoom);
                CursorMoved.event.fire(this);
            }
            case KeyEvent.VK_EQUALS -> {
                float zoom = camera.getZoom();
                zoom = Math.min(zoom + 0.25f, 2);
                camera.setZoom(zoom);
                CursorMoved.event.fire(this);
            }
        }
    }

    public void setColor(Color color) {
        this.color = color;
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

        int cursorWorldX = cursorX * tileSize;
        int cursorWorldY = cursorY * tileSize;

        velocityX = (cursorWorldX - camera.getX()) * 10;
        velocityY = (cursorWorldY - camera.getY()) * 10;

        camera.setX(camera.getX() + (velocityX * dt));
        camera.setY(camera.getY() + (velocityY * dt));

        velocityX *= 0.9;
        velocityY *= 0.9;
    }

    public void onRender(Renderer renderer) {
        int offset = 0;
        if ((mode == Mode.BLINKING || mode == Mode.DILATED) && timer < timerMax / 2) {
            offset = 5;
        }

        int size = tileSize + offset;
        int x = cursorX * tileSize - offset / 2;
        int y = cursorY * tileSize - offset / 2;

        var oldStroke = renderer.getLineWidth();
        renderer.setColor(Color.BLACK);
        renderer.setLineWidth(3);
        renderer.drawRect(x, y, size, size);
        renderer.setLineWidth(oldStroke);
        renderer.setColor(color);
        renderer.drawRect(x, y, size, size);
    }

    public int getCursorX() {
        return cursorX;
    }

    public int getCursorY() {
        return cursorY;
    }

    public void onMouseClicked(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();

        cursorX = (x / tileSize);
        cursorY = (y / tileSize);
        CursorMoved.event.fire(this);
    }

    public void onMouseWheelMoved(MouseEvent.WheelMoved event) {
        int wheelRotation = (int) event.getWheelRotation();
        float newZoom = camera.getZoom() + (Math.signum(wheelRotation) * -0.1f);
        newZoom = Util.clamp(newZoom, 0.25f, 4f);
        camera.setZoom(newZoom);
    }

    enum Mode {
        BLINKING,
        DILATED,
        NORMAL
    }
}
