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
    public ObserverPlayerController(BattleState state) {
        super(state);
    }

    @Override
    public void onMouseEvent(MouseEvent event) {
        switch (event) {
            case MouseEvent.Clicked clicked -> {
                state.getCursor().onMouseClicked(clicked);
                if (clicked.getButton() == Mouse.LEFT) {
                    this.selectActor();
                }
            }
            case MouseEvent.WheelMoved wheelMoved -> {
                state.getCursor().onMouseWheelMoved(wheelMoved);
            }
            default -> {}
        }
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

        state.getSelection().select(hovered.get());
        state.transitionTo(SelectMovePlayerController::new);
    }

    @Override
    public void onEnter() {
        state.getCursor().enterBlinkingMode();
        state.getCursor().setColor(Color.WHITE);
    }

    @Override
    public void onUpdate(Duration delta) {
        state.getCursor().onUpdate(delta);
    }

    @Override
    public void onWorldRender(Renderer renderer) {
        state.getCursor().onRender(renderer);
    }
}
