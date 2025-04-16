package game.state.battle.controller.player;

import game.event.Event;
import game.graphics.Renderer;
import game.input.Keyboard;
import game.state.battle.BattleState;
import game.state.battle.entity.Entity;
import game.state.battle.event.ActorHovered;

import java.time.Duration;

public class SelectActionPlayerController extends PlayerController {
//    private final StatsMenu statsMenu;
//    private final ActionsMenu actionsMenu;

    public SelectActionPlayerController(BattleState state) {
        super(state);

        Event<Entity> onChange = new Event<>();
//        statsMenu = new StatsMenu(20, 20, onChange);
//        statsMenu.setVisible(true);
//        onChange.fire(actor.get());

//        actionsMenu = new ActionsMenu(20, 210, state);
//        actionsMenu.setVisible(true);
//        actionsMenu.setLayout(new FormVerticalLayout(FormAlignment.CENTER));
    }

    @Override
    public final void onKeyPressed(int keycode) {
//        actionsMenu.onKeyPressed(keycode);

        if (keycode == Keyboard.SECONDARY) {
            if (!state.getSelection().isPresent()) {
                throw new IllegalStateException("No actor selected in the select action mode");
            }

            state.getSelection().clear();

            // The actor has issued its action, it is now waiting. So we can go back to the observer mode.
            int cursorX = state.getCursor().getCursorX();
            int cursorY = state.getCursor().getCursorY();
//            int actorX = (int) state.getSelection().get().getX();
//            int actorY = (int) state.getSelection().get().getY();
//            if (cursorX == actorX && cursorY == actorY) {
//                ActorHovered.event.fire(state.getSelection().get());
//            }

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

    @Override
    public void onGuiRender(Renderer renderer) {
//        statsMenu.onRender(graphics);
//        actionsMenu.onRender(graphics);
    }
}
