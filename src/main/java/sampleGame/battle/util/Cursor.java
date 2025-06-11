package sampleGame.battle.util;

import client.core.graphics.Renderer;
import client.core.input.KeyEvent;
import client.core.input.Keyboard;
import client.core.input.MouseEvent;
import client.core.util.Util;
import client.runtime.application.Application;
import java.awt.*;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import sampleGame.battle.BattleScene;

public class Cursor {
  private final Application game;
  private final BattleScene state;

  private final float timerMax = .75f;
  @Getter public int cursorX;
  @Getter public int cursorY;
  float velocityX;
  float velocityY;
  float accelerationX;
  float accelerationY;
  Mode mode = Mode.NORMAL;
  @Setter Color color = Color.RED;
  float timer = 0;

  public Cursor(Application game, BattleScene state) {
    this.game = game;
    this.state = state;

    cursorX = 0;
    cursorY = 0;
    velocityX = 0;
    velocityY = 0;
    accelerationX = 0;
    accelerationY = 0;
  }

  public void setPosition(int x, int y) {
    var newCursorX = Util.clamp(x, 0, state.getData().getWidth() - 1);
    var newCursorY = Util.clamp(y, 0, state.getData().getHeight() - 1);

    if (newCursorX == cursorX && newCursorY == cursorY) {
      return;
    }

    cursorX = newCursorX;
    cursorY = newCursorY;
    game.getAudio().play("type_preview/swipe");
    state.getController().onCursorMoved(this);
  }

  public void onKeyPressed(Integer keyCode) {
    switch (keyCode) {
      case Keyboard.LEFT -> {
        setPosition(cursorX - 1, cursorY);
      }
      case Keyboard.RIGHT -> {
        setPosition(cursorX + 1, cursorY);
      }
      case Keyboard.UP -> {
        setPosition(cursorX, cursorY - 1);
      }
      case Keyboard.DOWN -> {
        setPosition(cursorX, cursorY + 1);
      }
      case KeyEvent.VK_MINUS -> {
        float zoom = state.getCamera().getZoom();
        zoom = Math.max(zoom - 0.25f, 0.25f);
        state.getCamera().setZoom(zoom);
      }
      case KeyEvent.VK_EQUALS -> {
        float zoom = state.getCamera().getZoom();
        zoom = Math.min(zoom + 0.25f, 2);
        state.getCamera().setZoom(zoom);
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

    int cursorWorldX = cursorX * Application.TILE_SIZE;
    int cursorWorldY = cursorY * Application.TILE_SIZE;

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

    int size = Application.TILE_SIZE + offset;
    int x = cursorX * Application.TILE_SIZE - offset / 2;
    int y = cursorY * Application.TILE_SIZE - offset / 2;

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
    setPosition(x / Application.TILE_SIZE, y / Application.TILE_SIZE);
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
