package sampleGame.battle.controller;

import client.core.event.EventContext;
import client.core.graphics.Renderer;
import client.core.input.MouseEvent;
import java.time.Duration;
import sampleGame.battle.BattleScene;
import sampleGame.battle.util.Cursor;

public abstract class BattleController {
  protected final BattleScene state;
  protected final EventContext events = new EventContext();

  protected BattleController(BattleScene state) {
    this.state = state;
  }

  public abstract void onKeyPressed(int keyCode);

  public abstract void onEnter();

  public abstract void onUpdate(Duration delta);

  public abstract void onWorldRender(Renderer renderer);

  public void onExit() {
    events.clear();
  }

  public abstract boolean isDone();

  public void onCursorMoved(Cursor cursor) {}

  public void onMouseEvent(MouseEvent event) {}
}
