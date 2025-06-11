package sampleGame.battle.controller.player;

import client.core.graphics.Renderer;
import java.awt.*;
import java.time.Duration;
import sampleGame.battle.BattleScene;
import sampleGame.battle.util.Cursor;

public class SelectAttackPlayerController extends PlayerController {

  protected SelectAttackPlayerController(BattleScene state) {
    super(state);
  }

  @Override
  public void onKeyPressed(int keyCode) {
    state.getCursor().onKeyPressed(keyCode);
  }

  @Override
  public void onEnter() {
    state.getCursor().enterBlinkingMode();
    state.getCursor().setColor(Color.RED);
  }

  @Override
  public void onCursorMoved(Cursor cursor) {}

  @Override
  public void onUpdate(Duration delta) {
    state.getCursor().onUpdate(delta);
  }

  @Override
  public void onWorldRender(Renderer renderer) {}
}
