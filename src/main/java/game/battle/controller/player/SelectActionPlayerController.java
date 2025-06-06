package game.battle.controller.player;

import core.event.Event;
import core.graphics.Renderer;
import core.input.Keyboard;
import game.battle.BattleState;
import game.battle.entity.Entity;

import java.time.Duration;

public class SelectActionPlayerController extends PlayerController {
    public SelectActionPlayerController(BattleState state) {
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
    public void onEnter() {

    }

    @Override
    public void onUpdate(Duration delta) {
    }

    @Override
    public void onWorldRender(Renderer renderer) {

    }
}
