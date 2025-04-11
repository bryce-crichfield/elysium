package game.state.battle.controller;

import game.event.Event;
import game.platform.Renderer;
import game.state.battle.BattleState;
import game.state.battle.model.Actor;
import game.state.battle.model.Cursor;
import game.state.battle.model.Raycast;

import java.awt.*;
import java.time.Duration;

public class SelectAttackPlayerController extends PlayerController {
    private final game.event.Event<Actor> onChangeHovered;
    //    private final StatsMenu hoveredActorStats;
    Raycast raycast;

    protected SelectAttackPlayerController(BattleState state) {
        super(state);
        onChangeHovered = new Event<>();
//        hoveredActorStats = new StatsMenu(20, 20, onChangeHovered);
    }


//    public SelectAttackPlayerController(PlayerController controller) {
//        this(controller.world, controller.cursor, controller.actor.get(), controller.battleStateMachine);
//    }
//
//    public SelectAttackPlayerController(World world, game.state.battle.player.Cursor cursor, Actor actor, BattleStateMachine battleStateMachine) {
//        super(world, cursor, actor, battleStateMachine);
//
//        onChangeHovered = new Event<>();
//        hoveredActorStats = new StatsMenu(20, 20, onChangeHovered);
//        hoveredActorStats.setVisible(false);
//    }

    @Override
    public void onKeyPressed(int keyCode) {
        state.getCursor().onKeyPressed(keyCode);


//        if (keyCode == Keyboard.PRIMARY) {
////            getBattleState().getGame().getAudio().play("select.wav");
//            cursor.setPosition((int) actor.get().getX(), (int) actor.get().getY());
//            ActionActorAttack.event.fire(new ActionActorAttack(actor.get(), raycast.getTiles()));
//
//            // The actor has issued its attack, it is now waiting.
//            actor.get().setWaiting(true);
//            deselectActor();
//            battleStateMachine.transitionTo(new ObserverPlayerController(this));
////            ControllerTransition.defer.fire(() -> new ObserverPlayerController(world, cursor));
//        }
//
//        if (keyCode == Keyboard.SECONDARY) {
//            if (actor.isEmpty()) {
//                throw new IllegalStateException("No actor selected in the select action mode");
//            }
//            battleStateMachine.transitionTo(new SelectActionPlayerController(this));
////            ControllerTransition.defer.fire(() -> new SelectActionPlayerController(this));
//        }
    }

    @Override
    public void onEnter() {
        state.getCursor().enterBlinkingMode();
        state.getCursor().setColor(Color.RED);

//        on(CursorMoved.event).run(this::onCursorMoved);
    }

    private void onCursorMoved(Cursor cursor) {
//        int cursorX = cursor.getCursorX();
//        int cursorY = cursor.getCursorY();
//        raycast = world.raycast((int) actor.get().getX(), (int) actor.get().getY(), cursorX, cursorY);
//
//        Optional<Actor> hoveredActor = world.getActorByPosition(cursorX, cursorY);
//        if (hoveredActor.isEmpty()) {
//            hoveredActorStats.setVisible(false);
//        }
//
//        if (hoveredActor.isPresent()) {
//            if (hoveredActor.get() == this.actor.get()) {
//                hoveredActorStats.setVisible(false);
//                return;
//            }
//
//            Actor hovered = hoveredActor.get();
//            hoveredActorStats.setVisible(true);
//            onChangeHovered.fire(hovered);
//        }
    }

    @Override
    public void onUpdate(Duration delta) {
        state.getCursor().onUpdate(delta);
    }

    @Override
    public void onWorldRender(Renderer renderer) {

    }

    @Override
    public void onGuiRender(Renderer renderer) {

    }

//    @Override
//    public void onGuiRender(Graphics2D graphics) {
//        hoveredActorStats.onRender(graphics);
//    }

//    @Override
//    public void onWorldRender(Graphics2D graphics) {
//        cursor.onRender(graphics);
//
//        if (raycast == null) {
//            return;
//        }
//        List<Tile> possiblePath = raycast.getTiles();
//
//        Tile.drawOutline(possiblePath, graphics, Color.RED);
//
//        List<Tile> inRange = world.getTilesInRange((int) actor.get().getX(), (int) actor.get().getY(),
//                                                   actor.get().getAttackDistance()
//        );
//
//        Composite originalComposite = graphics.getComposite();
//        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
//        for (Tile tile : inRange) {
//            graphics.setColor(Color.RED.darker().darker());
//            graphics.fillRect(tile.getX() * 32, tile.getY() * 32, 32, 32);
//        }
//        graphics.setComposite(originalComposite);
//
//        Tile.drawOutline(inRange, graphics, Color.RED);
//    }

}
