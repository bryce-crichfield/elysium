package game.battle.controller.player;

import core.graphics.Renderer;
import core.input.Keyboard;
import core.input.Mouse;
import core.input.MouseEvent;
import game.battle.BattleState;
import game.battle.entity.Entity;
import game.battle.entity.components.TileAnimationComponent;
import game.battle.entity.components.PositionComponent;
import game.battle.event.ActionActorMoved;
import game.battle.tile.Tile;
import game.battle.tile.TilePath;
import game.battle.tile.TilePathFinder;
import game.battle.util.Cursor;
import core.util.Util;

import java.awt.*;
import java.time.Duration;

public class SelectMovePlayerController extends PlayerController {
    private TilePath path = new TilePath();

    public SelectMovePlayerController(BattleState state) {
        super(state);
    }

    @Override
    public void onEnter() {
        state.getCursor().enterBlinkingMode();
        state.getCursor().setColor(Color.ORANGE);

        events.on(ActionActorMoved.event).run(this::actorMoved);
    }

    public void actorMoved(ActionActorMoved movement) {
        Util.ensure(state.getSelection().isPresent(), "No actor selected in the select move mode");
    }

    @Override
    public void onCursorMoved(Cursor cursor) {
        var selectedActor = state.getSelection().get();
        if (selectedActor.hasComponent(PositionComponent.class)) {
            var position = selectedActor.getComponent(PositionComponent.class);
            var pathfinder = new TilePathFinder(state.getScene());
            var start = state.getScene().getTile((int) position.getX(), (int) position.getY());
            var end = state.getScene().getTile(cursor.getCursorX(), cursor.getCursorY());
            path = pathfinder.findPath(start, end);
        }
    }

    @Override
    public void onUpdate(Duration delta) {
        state.getCursor().onUpdate(delta);
    }

    @Override
    public void onWorldRender(Renderer renderer) {
        state.getCursor().onRender(renderer);
        drawMoveableArea(renderer);
    }

    private void drawMoveableArea(Renderer renderer) {
        var selected = state.getSelection().get();
        int distance = 5;

        if (selected.lacksComponent(PositionComponent.class)) return;

        var position = selected.getComponent(PositionComponent.class);
        float x = position.getX();
        float y = position.getY();
        var inRange = state.getScene().getTiles().within((int) x, (int) y, distance);
        var fillColor = new Color(255, 165, 0, 128); // Semi-transparent orange
        inRange.fillArea(renderer, fillColor);
        inRange.drawOutline(renderer, Color.ORANGE);
        path.drawPath(renderer, Color.ORANGE);
    }

    @Override
    public void onMouseEvent(MouseEvent event) {
        if (event instanceof MouseEvent.Moved) {
            onMouseMoved((MouseEvent.Moved) event);
        } else if (event instanceof MouseEvent.Clicked) {
            onMouseClicked((MouseEvent.Clicked) event);
        }
    }

    public void onMouseMoved(MouseEvent.Moved event) {
        // Set the cursor to where the mouse is
        int worldX = event.getX();
        int worldY = event.getY();
        int tileX = worldX / 32;
        int tileY = worldY / 32;

        // If outside the bounds of the moveable area, return
        Tile tile = state.getScene().getTile(tileX, tileY);
        if (tile == null) {
        }

//        state.getCursor().setPosition(tileX, tileY);
    }

    public void onMouseClicked(MouseEvent.Clicked event) {
        if (event.getButton() == Mouse.LEFT) {
            moveActor();
        }
    }

    void moveActor() {
        if (path.isEmpty()) {
            return;
        }

        int cursorX = state.getCursor().getCursorX();
        int cursorY = state.getCursor().getCursorY();
        boolean hoveringOnEmptyTile = state.getScene().findEntityByPosition(cursorX, cursorY).isEmpty();
        Entity entity = state.getSelection().get();

        if (!hoveringOnEmptyTile) return;

        if (entity.hasComponent(TileAnimationComponent.class)) {
            var animation = entity.getComponent(TileAnimationComponent.class);
            animation.start(entity, path);
        }

        path.clear();
        state.transitionTo(ObserverPlayerController::new);
    }

    @Override
    public void onKeyPressed(int keyCode) {
        state.getCursor().onKeyPressed(keyCode);

        if (keyCode == Keyboard.PRIMARY) {
            moveActor();
        }
    }
}

