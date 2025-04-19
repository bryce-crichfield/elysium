package game.state.overworld;

import game.Game;
import game.graphics.Renderer;
import game.graphics.Transform;
import game.graphics.background.Background;
import game.graphics.sprite.SpriteRenderer;
import game.state.GameState;
import game.state.battle.Scene;
import game.state.battle.entity.Entity;
import game.state.battle.entity.component.KeyboardComponent;
import game.state.battle.entity.components.*;
import game.state.battle.tile.Tile;
import game.state.battle.util.Camera;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.ArrayList;

public class OverworldState extends GameState {
    @Getter
    @Setter
    private final Camera camera;

    @Getter
    @Setter
    private final Scene scene;

    private final SpriteRenderer spriteRenderer = new SpriteRenderer("shaders/sprite/SpriteVertex.glsl", "shaders/sprite/SpriteFragment.glsl");


    public OverworldState(Game game) {
        super(game);
        addBackground(Background.stars());
        camera = new Camera(game);

        var tiles = new Tile[16][16];
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[x].length; y++) {
                var tile = new Tile(x, y, "tiles/Cyan", true);
                tiles[x][y] = tile;
            }
        }

        var entities = new ArrayList<Entity>();

        // Player Entity
        var entity = new Entity();
        entity.addComponent(new PositionComponent(6, 6));
        entity.addComponent(new SpriteComponent("sprites/test"));
        entity.addComponent(new KinematicsComponent());
        entity.addComponent(new PlayerMovementComponent());
        entities.add(entity);

        // Box that we can Collide Into
        var box = new Entity();
        box.addComponent(new PositionComponent(8, 8));
        entities.add(box);

        scene = new Scene(tiles, entities);

        var transform = Transform.orthographic(0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT, 0, -1, 1);
        spriteRenderer.setProjection(transform);

    }

    @Override
    public void onEnter() {

    }

    @Override
    public void onUpdate(Duration delta) {
        for (var entity : scene.getEntities()) {
            if (!entity.hasComponent(KeyboardComponent.class)) continue;
            entity.getAllComponents(KeyboardComponent.class)
                    .forEach(k -> k.onKeyboard(entity, game.getKeyboard()));
        }


        // find the player and make the camera follow them
        var player = scene.getEntities().stream()
                .filter(e -> e.hasComponent(PlayerMovementComponent.class))
                .findFirst()
                .orElse(null);

        // Verify camera updated correctly
        System.out.println("Camera after update: " + camera.getX() + "," + camera.getY());

        scene.onUpdate(delta);

    }

    @Override
    public void onRender(Renderer renderer) {
        var cameraTransform = camera.getTransform();
        renderer.pushTransform(cameraTransform);
        spriteRenderer.setView(cameraTransform);
        scene.onRender(renderer, spriteRenderer);
        renderer.popTransform();
    }
}
