package game.state.battle.controller;

import game.input.MouseEvent;
import game.event.EventContext;
import game.platform.Renderer;
import game.state.battle.BattleState;

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

    public abstract void onWorldRender(Renderer renderer);

    public abstract void onGuiRender(Renderer renderer);

    public void onExit() {
        events.clear();
    }


    public abstract boolean isDone();

    public void onMouseClicked(MouseEvent.Clicked event) {

    }

    public void onMouseWheelMoved(MouseEvent.WheelMoved event) {

    }
}
