package game.state.battle.controller.player;

import game.graphics.Renderer;
import game.input.Keyboard;
import game.input.Mouse;
import game.input.MouseEvent;
import game.state.battle.BattleState;
import game.state.battle.entity.Entity;
import game.state.battle.entity.components.PositionComponent;
import game.state.battle.event.ActionActorMoved;
import game.state.battle.util.Cursor;
import game.state.battle.util.Pathfinder;
import game.state.battle.Tile;
import game.util.Util;

import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SelectMovePlayerController extends PlayerController {
//    private final StatsMenu selectedActorStats;
//    private final StatsMenu hoveredActorStats;

    private List<Tile> possiblePath;

    public SelectMovePlayerController(BattleState state) {
        super(state);

        possiblePath = new ArrayList<>();

//        selectedActorStats = new StatsMenu(20, 20, onChangeSelected);
//        selectedActorStats.setVisible(true);

//        hoveredActorStats = new StatsMenu(280, 20, onChangeHovered);
//        hoveredActorStats.setVisible(false);
    }

    @Override
    public void onEnter() {
        state.getCursor().enterBlinkingMode();
        state.getCursor().setColor(Color.ORANGE);

        events.on(ActionActorMoved.event).run(this::actorMoved);
    }

    public void actorMoved(ActionActorMoved movement) {
        Util.ensure(state.getSelection().isPresent(), "No actor selected in the select move mode");

        // The actor has now moved and can no longer move this turn, it is waiting for the next turn
//        if (state.getSelection().get().getVitals().movementPoints <= 0) {
//            state.getSelection().get().setWaiting(true);
//            state.getSelection().clear();
//            state.transitionTo(ObserverPlayerController::new);
//        } else {
//            state.transitionTo(SelectActionPlayerController::new);
//        }
    }

    @Override
    public void onCursorMoved(Cursor cursor) {
        int cursorX = cursor.getCursorX();
        int cursorY = cursor.getCursorY();

        Optional<Entity> hoveredActor = state.getScene().getActorByPosition(cursorX, cursorY);
        boolean hoveringOnEmptyTile = hoveredActor.isEmpty();
        if (hoveringOnEmptyTile) {
            Pathfinder pathfinder = new Pathfinder(state.getScene(), state.getSelection().get());
            var selectedActor = state.getSelection().get();
            if (selectedActor.lacksComponent(PositionComponent.class)) return;
            var position = selectedActor.getComponent(PositionComponent.class);
            int actorX = (int) position.getX();
            int actorY = (int) position.getY();
            Tile start = state.getScene().getTile(actorX, actorY);
            Tile end = state.getScene().getTile(cursorX, cursorY);
            possiblePath = pathfinder.find(start, end);

//            hoveredActorStats.setVisible(false);
        } else {
            possiblePath = new ArrayList<>();

            if (hoveredActor.get().equals(state.getSelection().get())) {
//                hoveredActorStats.setVisible(false);
                return;
            }

            Entity hovered = hoveredActor.get();
//            hoveredActorStats.setVisible(true);
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
//        int distance = state.getSelection().get().getVitals().movementPoints;
        int distance = 5;

        if (selected.lacksComponent(PositionComponent.class)) return;

        var position = selected.getComponent(PositionComponent.class);
        int x = (int) position.getX();
        int y = (int) position.getY();
        List<Tile> inRange = state.getScene().getTilesInRange(x, y, distance);

        for (Tile tile : inRange) {
            var color = Color.ORANGE.darker().darker();
            color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 100);
            renderer.setColor(color);
            renderer.fillRect(tile.getX() * 32, tile.getY() * 32, 32, 32);
        }

        Tile.drawOutline(inRange, renderer, Color.ORANGE);
        Tile.drawTurtle(possiblePath, renderer, Color.ORANGE);
    }

    @Override
    public void onGuiRender(Renderer renderer) {
//        selectedActorStats.onSpriteRender(graphics);
//        hoveredActorStats.onSpriteRender(graphics);
    }

    @Override
    public void onMouseMoved(MouseEvent.Moved event) {
        // Set the cursor to where the mouse is
        int worldX = event.getX();
        int worldY = event.getY();
        int tileX = worldX / 32;
        int tileY = worldY / 32;

        // If outside the bounds of the moveable area, return
        Tile tile = state.getScene().getTile(tileX, tileY);
        if (tile == null) {
            return;
        }

        state.getCursor().setPosition(tileX, tileY);
    }

    @Override
    public void onMouseClicked(MouseEvent.Clicked event) {
        if (event.getButton() == Mouse.LEFT) {
            moveActor();
        }
    }

    void moveActor() {
        if (possiblePath.isEmpty()) {
            return;
        }

        int cursorX = state.getCursor().getCursorX();
        int cursorY = state.getCursor().getCursorY();
        boolean hoveringOnEmptyTile = state.getScene().getActorByPosition(cursorX, cursorY).isEmpty();
        Entity entity = state.getSelection().get();

        if (!hoveringOnEmptyTile) return;


        // getBattleState().getGame().getAudio().play("select.wav");
//        entity.move(possiblePath);
        possiblePath = new ArrayList<>();
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

