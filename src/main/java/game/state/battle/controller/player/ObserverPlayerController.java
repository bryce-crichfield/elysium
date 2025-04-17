package game.state.battle.controller.player;

import game.graphics.Renderer;
import game.input.Keyboard;
import game.input.Mouse;
import game.input.MouseEvent;
import game.state.battle.BattleState;
import game.state.battle.entity.Entity;
import game.state.battle.util.Cursor;

import java.awt.*;
import java.time.Duration;
import java.util.Optional;

public class ObserverPlayerController extends PlayerController {
//    private final StatsMenu hoveredActorStats;

    public ObserverPlayerController(BattleState state) {
        super(state);
//        hoveredActorStats = new StatsMenu(20, 20, ActorSelected.event);
    }

    @Override
    public void onMouseEvent(MouseEvent event) {
        switch (event) {
            case MouseEvent.Clicked clicked -> onMouseClicked(clicked);
            case MouseEvent.WheelMoved wheelMoved -> onMouseWheelMoved(wheelMoved);
            default -> {}
        }
    }

    private void onMouseClicked(MouseEvent.Clicked event) {
        state.getCursor().onMouseClicked(event);
        if (event.getButton() == Mouse.LEFT) {
            this.selectActor();
        }
    }

    private void onMouseWheelMoved(MouseEvent.WheelMoved event) {
        state.getCursor().onMouseWheelMoved(event);
    }

    @Override
    public void onKeyPressed(int keyCode) {
        state.getCursor().onKeyPressed(keyCode);
        if (keyCode == Keyboard.PRIMARY) {
            this.selectActor();
        }
    }

    public final void selectActor() {
        int cursorX = state.getCursor().getCursorX();
        int cursorY = state.getCursor().getCursorY();

        Optional<Entity> hovered = state.getScene().findEntityByPosition(cursorX, cursorY);

        if (hovered.isEmpty()) {
            return;
        }

//        if (hovered.get().isPlayer()) {
//            if (hovered.get().isWaiting())
//                return;
        state.getSelection().select(hovered.get());
        state.transitionTo(SelectMovePlayerController::new);

//            ActorSelected.event.fire(actor.get());
//        }
    }

    private void forceHoveredActorStats() {
        int cx = state.getCursor().getCursorX();
        int cy = state.getCursor().getCursorY();
        Optional<Entity> hov = state.getScene().findEntityByPosition(cx, cy);
        hov.ifPresent(actor -> {
//            onChangeHovered.fire(actor);
//            hoveredActorStats.setVisible(true);
        });
    }

    @Override
    public void onEnter() {
        forceHoveredActorStats();

        state.getCursor().enterBlinkingMode();
        state.getCursor().setColor(Color.WHITE);

//        context.addListener(ActorSelected.event).run(actor -> {
//            battleStateMachine.transitionTo(new SelectActionPlayerController(this));
//            ControllerTransition.defer.fire(() -> new SelectActionPlayerController(this));
//        });

//        on(CursorMoved.event).run(this::hoverActor);
    }

    @Override
    public void onUpdate(Duration delta) {
        state.getCursor().onUpdate(delta);
    }

    @Override
    public void onWorldRender(Renderer renderer) {
        state.getCursor().onRender(renderer);
    }

    @Override
    public void onGuiRender(Renderer renderer) {
//        hoveredActorStats.onSpriteRender(renderer);
    }

    private void hoverActor(Cursor cursor) {
        int cursorX = state.getCursor().getCursorX();
        int cursorY = state.getCursor().getCursorY();

        Optional<Entity> actor = state.getScene().findEntityByPosition(cursorX, cursorY);
        if (actor.isEmpty()) {
//            hoveredActorStats.setVisible(false);
        }

        if (actor.isPresent()) {
            Entity hovered = actor.get();
//            hoveredActorStats.setVisible(true);
//            onChangeHovered.fire(hovered);
        }
    }
}
