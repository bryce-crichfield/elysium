package game.battle;

import core.GameContext;
import core.graphics.Renderer;
import core.graphics.Transform;
import core.graphics.background.Background;
import core.graphics.sprite.SpriteRenderer;
import core.gui.container.GuiContainer;
import core.gui.input.GuiEventState;
import core.gui.layout.GuiNullLayout;
import core.input.MouseEvent;
import core.state.GameState;
import game.battle.controller.BattleController;
import game.battle.controller.BattleControllerFactory;
import game.battle.controller.player.ObserverPlayerController;
import game.battle.entity.Entity;
import game.battle.entity.components.AnimationComponent;
import game.battle.entity.components.TileAnimationComponent;
import game.battle.entity.components.PositionComponent;
import game.battle.entity.components.SpriteComponent;
import game.battle.hud.ActionsMenu;
import game.battle.hud.EntityInspector;
import game.battle.tile.Tile;
import game.battle.util.Camera;
import game.battle.util.Cursor;
import game.battle.util.Selection;
import game.title.TitleState;
import core.transition.Transitions;
import core.util.Easing;
import lombok.Getter;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.util.ArrayList;

public class BattleState extends GameState {
    @Getter
    private final Camera camera;
    @Getter
    private final Scene scene;
    @Getter
    private final Cursor cursor;

    private final SpriteRenderer spriteRenderer = new SpriteRenderer("shaders/sprite/SpriteVertex.glsl", "shaders/sprite/SpriteFragment.glsl");

    @Getter
    private final Selection selection = new Selection();
    @Getter
    private final GuiContainer gui = new GuiContainer(0, 0, GameContext.SCREEN_WIDTH, GameContext.SCREEN_HEIGHT);
    @Getter
    private BattleController controller = new ObserverPlayerController(this);

    public BattleState(GameContext gameContext) {
        super(gameContext);
        camera = new Camera(gameContext);

        var tiles = new Tile[16][16];
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[x].length; y++) {
                var tile = new Tile(x, y, "tiles/Cyan", true);
                tiles[x][y] = tile;
            }
        }

        var entities = new ArrayList<Entity>();
        var entity = new Entity();
        entity.addComponent(new PositionComponent(6, 6));
        entity.addComponent(new SpriteComponent("sprites/test"));
        entity.addComponent(new AnimationComponent());
        entity.addComponent(new TileAnimationComponent());
        entities.add(entity);

        scene = new Scene(tiles, entities);
        Scene.serialize("scene1", scene);
//        scene = Scene.deserialize("scene1");
        cursor = new Cursor(gameContext, this);
        addBackground(Background.stars());

        transitionTo(ObserverPlayerController::new);

        var transform = Transform.orthographic(0, GameContext.SCREEN_WIDTH, GameContext.SCREEN_HEIGHT, 0, -1, 1);
        spriteRenderer.setProjection(transform);

        gui.addChild(new ActionsMenu(this, 0, 0));
        gui.addChild(new EntityInspector(this, GameContext.SCREEN_WIDTH - 500, 0));
        gui.setLayout(new GuiNullLayout());
    }

    @Override
    public void onEnter() {
        gameContext.getAudio().play("ambience/ambience_spacecraft_hold_loop", true, 0.25f);
    }

    @Override
    public void onExit() {
        controller.onExit();
    }

    @Override
    public void onMouseEvent(MouseEvent event) {
        if (gui.processMouseEvent(event) == GuiEventState.CONSUMED) {
            return;
        }

        var worldX = camera.getWorldX(event.getX());
        var worldY = camera.getWorldY(event.getY());
        event = event.withPoint(new Point(worldX, worldY));

        controller.onMouseEvent(event);
    }

    public void onKeyPressed(int keycode) {
        controller.onKeyPressed(keycode);

        if (keycode == KeyEvent.VK_ESCAPE) {
            gameContext.pushState(TitleState::new, Transitions.fade(Duration.ofMillis(1000), Color.BLACK, Easing.cubicEaseIn()));
        }
    }

    @Override
    public void onUpdate(Duration delta) {
        gui.update(delta);
        scene.onUpdate(delta);
        controller.onUpdate(delta);
    }

    @Override
    public void onRender(Renderer renderer) {
        // Get the camera worldTransform and render the world
        Transform worldTransform = camera.getTransform();
        renderer.pushTransform(worldTransform);
        spriteRenderer.setView(worldTransform);
        scene.onRender(renderer, spriteRenderer);
        controller.onWorldRender(renderer);
        renderer.popTransform();

        gui.render(renderer);
    }

    public void transitionTo(BattleControllerFactory factory) {
        controller.onExit();
        controller = factory.create(this);
        controller.onEnter();
    }
}
