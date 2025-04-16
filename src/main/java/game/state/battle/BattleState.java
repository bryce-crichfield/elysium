package game.state.battle;

import game.Game;
import game.graphics.Renderer;
import game.graphics.Transform;
import game.graphics.background.StarBackground;
import game.graphics.sprite.SpriteRenderer;
import game.input.MouseEvent;
import game.state.GameState;
import game.state.battle.controller.BattleController;
import game.state.battle.controller.BattleControllerFactory;
import game.state.battle.controller.player.ObserverPlayerController;
import game.state.battle.entity.Entity;
import game.state.battle.util.Camera;
import game.state.battle.util.Cursor;
import game.state.battle.util.Selection;
import game.state.battle.world.World;
import game.state.title.TitleState;
import game.transition.Transitions;
import game.util.Easing;
import lombok.Getter;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.util.Optional;

public class BattleState extends GameState {
    @Getter
    private final Camera camera;
    @Getter
    private final World world;
    @Getter
    private final Cursor cursor;

    private final SpriteRenderer spriteRenderer = new SpriteRenderer("shaders/sprite/SpriteVertex.glsl", "shaders/sprite/SpriteFragment.glsl");

    @Getter
    private final Selection selection = new Selection();

    private Optional<BattleController> currentController = Optional.empty();

    public BattleState(Game game) {
        super(game);
        camera = new Camera(game);
        world = new World(16, 16);
        cursor = new Cursor(camera, game, world);
        addBackground(StarBackground::new);

        for (Entity entity : world.getEntities()) {
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

        var transform = Transform.orthographic(0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT, 0, -1, 1);
        spriteRenderer.setProjection(transform);
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
        var e = event.withPoint(new Point(worldX, worldY));
        currentController.ifPresent(c -> c.onMouseClicked(e));
    }

    @Override
    public void onMouseMoved(MouseEvent.Moved event) {
        // translate from screen coordinates to world coordinates
        int worldX = camera.getWorldX(event.getX());
        int worldY = camera.getWorldY(event.getY());
        var e = event.withPoint(new Point(worldX, worldY));
        currentController.ifPresent(c -> c.onMouseMoved(e));
    }

    @Override
    public void onMouseWheelMoved(MouseEvent.WheelMoved event) {
        // translate from screen coordinates to world coordinates
        int worldX = camera.getWorldX(event.getX());
        int worldY = camera.getWorldY(event.getY());
        var e = event.withPoint(new Point(worldX, worldY));
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
        // Get the camera worldTransform and render the world
        Transform worldTransform = camera.getTransform();
        renderer.pushTransform(worldTransform);
        spriteRenderer.setView(worldTransform);
        world.onRender(renderer, spriteRenderer);
        currentController.ifPresent(c -> c.onWorldRender(renderer));
        renderer.popTransform();

        // Restore the original worldTransform and draw the gui
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
}
