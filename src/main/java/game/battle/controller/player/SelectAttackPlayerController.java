package game.battle.controller.player;

import core.graphics.Renderer;
import game.battle.BattleState;
import game.battle.util.Cursor;

import java.awt.*;
import java.time.Duration;

public class SelectAttackPlayerController extends PlayerController {

    protected SelectAttackPlayerController(BattleState state) {
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
    public void onCursorMoved(Cursor cursor) {

    }

    @Override
    public void onUpdate(Duration delta) {
        state.getCursor().onUpdate(delta);
    }

    @Override
    public void onWorldRender(Renderer renderer) {

    }
}
