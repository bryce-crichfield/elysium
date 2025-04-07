package game.state.battle.controller;

import game.event.EventContext;
import game.state.battle.state.BattleState;

import java.awt.*;
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
    public abstract void onWorldRender(Graphics2D graphics);
    public abstract void onGuiRender(Graphics2D graphics);

    public void onExit() {
        events.clear();
    }


    public abstract boolean isDone();
}
