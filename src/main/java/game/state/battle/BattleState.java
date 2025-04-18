package game.state.battle;

import game.Game;
import game.graphics.Renderer;
import game.graphics.Transform;
import game.graphics.background.Background;
import game.graphics.sprite.SpriteRenderer;
import game.gui.container.GuiContainer;
import game.gui.input.GuiEventState;
import game.gui.layout.GuiNullLayout;
import game.input.MouseEvent;
import game.state.GameState;
import game.state.battle.controller.BattleController;
import game.state.battle.controller.BattleControllerFactory;
import game.state.battle.controller.player.ObserverPlayerController;
import game.state.battle.entity.Entity;
import game.state.battle.entity.components.PositionComponent;
import game.state.battle.entity.components.SpriteComponent;
import game.state.battle.hud.ActionsMenu;
import game.state.battle.hud.EntityInspector;
import game.state.battle.tile.Tile;
import game.state.battle.util.Camera;
import game.state.battle.util.Cursor;
import game.state.battle.util.Selection;
import game.state.title.TitleState;
import game.transition.Transitions;
import game.util.Easing;
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
    private final GuiContainer gui = new GuiContainer(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
    @Getter
    private BattleController controller = new ObserverPlayerController(this);

    public BattleState(Game game) {
        super(game);
        camera = new Camera(game);

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
        entities.add(entity);

        scene = new Scene(tiles, entities);
        Scene.serialize("scene1", scene);
//        scene = Scene.deserialize("scene1");
        cursor = new Cursor(game, this);
        addBackground(Background.stars());

        transitionTo(ObserverPlayerController::new);

        var transform = Transform.orthographic(0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT, 0, -1, 1);
        spriteRenderer.setProjection(transform);

        gui.addChild(new ActionsMenu(this, 0, 0));
        gui.addChild(new EntityInspector(this, Game.SCREEN_WIDTH - 500, 0));
        gui.setLayout(new GuiNullLayout());
    }

    @Override
    public void onEnter() {
        game.getAudio().play("ambience/ambience_spacecraft_hold_loop", true, 0.25f);
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
            game.pushState(TitleState::new, Transitions.fade(Duration.ofMillis(1000), Color.BLACK, Easing.cubicEaseIn()));
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
