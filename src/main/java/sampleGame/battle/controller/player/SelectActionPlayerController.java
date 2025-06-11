package sampleGame.battle.controller.player;

import client.core.event.Event;
import client.core.graphics.Renderer;
import client.core.input.Keyboard;
import java.time.Duration;
import sampleGame.battle.BattleScene;
import sampleGame.data.entity.Entity;

public class SelectActionPlayerController extends PlayerController {
  public SelectActionPlayerController(BattleScene state) {
    super(state);

    Event<Entity> onChange = new Event<>();
  }

  @Override
  public final void onKeyPressed(int keycode) {
    if (keycode == Keyboard.SECONDARY) {
      if (!state.getSelection().isPresent()) {
        throw new IllegalStateException("No actor selected in the select action mode");
      }

      state.getSelection().clear();

      int cursorX = state.getCursor().getCursorX();
      int cursorY = state.getCursor().getCursorY();

      state.transitionTo(ObserverPlayerController::new);
    }
  }

  @Override
  public void onEnter() {}

  @Override
  public void onUpdate(Duration delta) {}

  @Override
  public void onWorldRender(Renderer renderer) {}
}
