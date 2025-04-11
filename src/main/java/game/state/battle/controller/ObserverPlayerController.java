package game.state.battle.controller;

import game.input.Keyboard;
import game.platform.Renderer;
import game.state.battle.BattleState;
import game.state.battle.model.Actor;
import game.state.battle.model.Cursor;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.time.Duration;
import java.util.Optional;

public class ObserverPlayerController extends PlayerController {
//    private final StatsMenu hoveredActorStats;

    public ObserverPlayerController(BattleState state) {
        super(state);
//        hoveredActorStats = new StatsMenu(20, 20, ActorSelected.event);
    }


    @Override
    public void onMouseClicked(MouseEvent event) {
        state.getCursor().onMouseClicked(event);
        if (event.getButton() == MouseEvent.BUTTON1) {
            this.selectActor();
        }
    }

    @Override
    public void onMouseWheelMoved(MouseWheelEvent event) {
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

        Optional<Actor> hovered = state.getWorld().getActorByPosition(cursorX, cursorY);

        if (hovered.isEmpty()) {
            return;
        }

        if (hovered.get().isPlayer()) {
            if (hovered.get().isWaiting())
                return;
            state.getSelection().select(hovered.get());
            state.transitionTo(SelectActionPlayerController::new);

//            ActorSelected.event.fire(actor.get());
        }
    }

    private void forceHoveredActorStats() {
        int cx = state.getCursor().getCursorX();
        int cy = state.getCursor().getCursorY();
        Optional<Actor> hov = state.getWorld().getActorByPosition(cx, cy);
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
//        hoveredActorStats.onRender(renderer);
    }

    private void hoverActor(Cursor cursor) {
        int cursorX = state.getCursor().getCursorX();
        int cursorY = state.getCursor().getCursorY();

        Optional<Actor> actor = state.getWorld().getActorByPosition(cursorX, cursorY);
        if (actor.isEmpty()) {
//            hoveredActorStats.setVisible(false);
        }

        if (actor.isPresent()) {
            Actor hovered = actor.get();
//            hoveredActorStats.setVisible(true);
//            onChangeHovered.fire(hovered);
        }
    }
}
