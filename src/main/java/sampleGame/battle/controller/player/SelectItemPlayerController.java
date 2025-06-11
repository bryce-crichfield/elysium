package sampleGame.battle.controller.player;

import client.core.graphics.Renderer;
import java.time.Duration;
import sampleGame.battle.BattleScene;

public class SelectItemPlayerController extends PlayerController {
  public SelectItemPlayerController(BattleScene state) {
    super(state);
  }

  @Override
  public void onKeyPressed(int keyCode) {}

  @Override
  public void onEnter() {}

  @Override
  public void onUpdate(Duration delta) {}

  @Override
  public void onWorldRender(Renderer renderer) {}
}
