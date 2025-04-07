package game.state.battle.controller;

import game.io.Keyboard;
import game.state.battle.event.*;
//import game.state.battle.hud.StatsMenu;
import game.state.battle.model.Pathfinder;
import game.state.battle.model.Tile;
import game.state.battle.model.Actor;
import game.event.Event;
import game.state.battle.state.BattleState;
import game.state.battle.state.Cursor;
import game.util.Util;

import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SelectMovePlayerController extends PlayerController {
//    private final StatsMenu selectedActorStats;
//    private final StatsMenu hoveredActorStats;

    Event<Actor> onChangeHovered = new Event<>();
    private List<Tile> possiblePath;

    public SelectMovePlayerController(BattleState state) {
        super(state);

        possiblePath = new ArrayList<>();

        Event<Actor> onChangeSelected = new Event<>();
//        selectedActorStats = new StatsMenu(20, 20, onChangeSelected);
//        selectedActorStats.setVisible(true);
        onChangeSelected.fire(state.getSelection().get());

//        hoveredActorStats = new StatsMenu(280, 20, onChangeHovered);
//        hoveredActorStats.setVisible(false);
        onChangeHovered.fire(state.getSelection().get());
    }

    @Override
    public void onEnter() {
        state.getCursor().enterBlinkingMode();
        state.getCursor().setColor(Color.ORANGE);

        events.on(CursorMoved.event).run(this::onCursorMoved);
        events.on(ActionActorMoved.event).run(this::actorMoved);
    }

    public void actorMoved(ActionActorMoved movement) {
        Util.ensure(state.getSelection().isPresent(), "No actor selected in the select move mode");

        // the actor has now moved and can no longer move this turn, it is waiting for the next turn
        if (state.getSelection().get().getMovementPoints() <= 0) {
            state.getSelection().get().setWaiting(true);
            state.getSelection().clear();
            state.transitionTo(ObserverPlayerController::new);
        } else {
            state.transitionTo(SelectActionPlayerController::new);
        }
    }

    private void onCursorMoved(Cursor cursor) {
        int cursorX = cursor.getCursorX();
        int cursorY = cursor.getCursorY();

        Optional<Actor> hoveredActor = state.getWorld().getActorByPosition(cursorX, cursorY);
        boolean hoveringOnEmptyTile = hoveredActor.isEmpty();
        if (hoveringOnEmptyTile) {
            Pathfinder pathfinder = new Pathfinder(state.getWorld(), state.getSelection().get());
            int actorX = (int) state.getSelection().get().getX();
            int actorY = (int) state.getSelection().get().getY();
            Tile start = state.getWorld().getTile(actorX, actorY);
            Tile end = state.getWorld().getTile(cursorX, cursorY);
            possiblePath = pathfinder.find(start, end);

//            hoveredActorStats.setVisible(false);
        } else {
            possiblePath = new ArrayList<>();

            if (hoveredActor.get().equals(state.getSelection().get())) {
//                hoveredActorStats.setVisible(false);
                return;
            }

            Actor hovered = hoveredActor.get();
//            hoveredActorStats.setVisible(true);
            onChangeHovered.fire(hovered);
        }
    }

    @Override
    public void onUpdate(Duration delta) {
        state.getCursor().onUpdate(delta);
    }

    @Override
    public void onWorldRender(Graphics2D graphics) {
//        selectedActorStats.onRender(graphics);
//        hoveredActorStats.onRender(graphics);

        int distance = state.getSelection().get().getMovementPoints();
        int actorX = (int) state.getSelection().get().getX();
        int actorY = (int) state.getSelection().get().getY();
        List<Tile> inRange = state.getWorld().getTilesInRange(actorX, actorY, distance);

        Composite originalComposite = graphics.getComposite();
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
        for (Tile tile : inRange) {
            graphics.setColor(Color.ORANGE.darker().darker());
            graphics.fillRect(tile.getX() * 32, tile.getY() * 32, 32, 32);
        }
        graphics.setComposite(originalComposite);

        Tile.drawOutline(inRange, graphics, Color.ORANGE);


        Tile.drawTurtle(possiblePath, graphics, Color.ORANGE);

        state.getCursor().onRender(graphics);
    }

    @Override
    public void onGuiRender(Graphics2D graphics) {

    }


    @Override
    public void onKeyPressed(int keyCode) {
        state.getCursor().onKeyPressed(keyCode);

        boolean primaryPressed = keyCode == Keyboard.PRIMARY;
        int cursorX = state.getCursor().getCursorX();
        int cursorY = state.getCursor().getCursorY();
        boolean hoveringOnEmptyTile = state.getWorld().getActorByPosition(cursorX, cursorY).isEmpty();

        if (hoveringOnEmptyTile && primaryPressed) {
//            getBattleState().getGame().getAudio().play("select.wav");
            if (possiblePath.isEmpty()) {
                return;
            }

            ActionActorMoved.event.fire(new ActionActorMoved(state.getSelection().get(), possiblePath));
            possiblePath = new ArrayList<>();
        }

//        if (keyCode == Keyboard.SECONDARY) {
//            if (actor.isEmpty()) {
//                throw new IllegalStateException("No actor selected in the select action mode");
//            }
//            battleStateMachine.transitionTo(new SelectActionPlayerController(this));
////            ControllerTransition.defer.fire(() -> new SelectActionPlayerController(this));
        }
    }

