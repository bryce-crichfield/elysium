package game.state.battle;

import game.Game;
import game.graphics.background.StarBackground;
import game.input.Mouse;
import game.input.MouseEvent;
import game.platform.Renderer;
import game.state.GameState;
import game.state.battle.controller.BattleController;
import game.state.battle.controller.BattleControllerFactory;
import game.state.battle.controller.ObserverPlayerController;
import game.state.battle.model.Actor;
import game.state.battle.model.Cursor;
import game.state.battle.model.Selection;
import game.state.battle.model.World;
import game.state.title.TitleState;
import game.transition.Transitions;
import game.util.Camera;
import game.util.Easing;
import lombok.Getter;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.util.Optional;

public class BattleState extends GameState {
    private final Camera camera;
    private final World world;
    private final Cursor cursor;

    @Getter
    private final Selection selection = new Selection();

    private Optional<BattleController> currentController = Optional.empty();

    public BattleState(Game game) {
        super(game);
        camera = new Camera(game);
        world = new World(16, 16);
        cursor = new Cursor(camera, game, world);
        addBackground(StarBackground::new);

        for (Actor actor : world.getActors()) {
//            getSubscriptions().on(ActionActorMoved.event).run(actor::onActorMoved);
//            getSubscriptions().on(ActorSelected.event).run(a -> {
//                if (a.equals(actor)) {
//                    actor.setSelected(true);
//                } else {
//                    actor.setSelected(false);
//                }
//            });
//            getSubscriptions().on(ActorUnselected.event).run(actor::onActorDeselected);
//            getSubscriptions().on(ActionActorAttack.event).run(actor::onActorAttacked);
//            getSubscriptions().on(ActorKilled.event).run(world::removeActor);
//            getSubscriptions().on(CursorMoved.event).run(actor::onCursorMoved);
        }

        transitionTo(ObserverPlayerController::new);
    }

    @Override
    public void onEnter() {
        game.getAudio().play("ambience/ambience_spacecraft_hold_loop", true, 0.25f);
    }

    @Override
    public void onMouseClicked(MouseEvent.Clicked event) {
        // translate from screen coordinates to world coordinates
        int worldX = camera.getWorldX(event.getX());
        int worldY = camera.getWorldY(event.getY());
//        var e = Mouse.translateEvent(event, worldX, worldY);
        var e = event.withPoint(new Point(worldX, worldY));
        currentController.ifPresent(c -> c.onMouseClicked(e));
    }

    @Override
    public void onMouseWheelMoved(MouseEvent.WheelMoved event) {
        // translate from screen coordinates to world coordinates
        int worldX = camera.getWorldX(event.getX());
        int worldY = camera.getWorldY(event.getY());
        var e = event.withPoint(new Point(worldX, worldY));
//        var e = (MouseWheelEvent) Mouse.translateEvent(event, worldX, worldY);
        currentController.ifPresent(c -> c.onMouseWheelMoved(e));
    }

    public void onKeyPressed(int keycode) {
        currentController.ifPresent(c -> c.onKeyPressed(keycode));

        if (keycode == KeyEvent.VK_ESCAPE) {
            game.pushState(TitleState::new, Transitions.fade(Duration.ofMillis(1000), Color.BLACK, Easing.cubicEaseIn()));
        }
    }

    @Override
    public void onUpdate(Duration delta) {
        world.onUpdate(delta);

//        if (getGame().getKeyboard().pressed(KeyEvent.VK_ESCAPE)) {
//            getGame().popState();
//        }

        currentController.ifPresent(c -> c.onUpdate(delta));
    }

    @Override
    public void onRender(Renderer renderer) {
        // Render the star background

        // Get the camera worldTransform and render the world
        var guiTransform = renderer.getTransform();
        var worldTransform = camera.getTransform();
        renderer.setTransform(worldTransform);
        world.onRender(renderer);
        currentController.ifPresent(c -> c.onWorldRender(renderer));

        // Restore the original worldTransform and draw the gui
        renderer.setTransform(guiTransform);
        currentController.ifPresent(c -> c.onGuiRender(renderer));

    }

    /**
     * Transitions the current controller to a new controller created by the given factory.
     * If there is an existing controller, it will call its onExit method before transitioning.
     * The new controller's onEnter method will be called after it is created.
     *
     * @param factory the factory to create the new controller
     */
    public void transitionTo(BattleControllerFactory factory) {
        currentController.ifPresent(c -> c.onExit());
        currentController = Optional.of(factory.create(this));
        currentController.ifPresent(c -> c.onEnter());
    }

    public Cursor getCursor() {
        return cursor;
    }

    public World getWorld() {
        return world;
    }
}
