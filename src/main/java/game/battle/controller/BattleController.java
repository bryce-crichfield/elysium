package game.battle.controller;

import core.event.EventContext;
import core.graphics.Renderer;
import core.input.MouseEvent;
import game.battle.BattleState;
import game.battle.util.Cursor;

import java.time.Duration;

public abstract class BattleController {
    protected final BattleState state;
    protected final EventContext events = new EventContext();

    protected BattleController(BattleState state) {
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


    public void onCursorMoved(Cursor cursor) {
    }

    public void onMouseEvent(MouseEvent event) {
    }
}
