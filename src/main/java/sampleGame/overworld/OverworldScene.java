package sampleGame.overworld;

import client.core.graphics.Renderer;
import client.core.graphics.Transform;
import client.core.graphics.background.Background;
import client.core.graphics.sprite.SpriteRenderer;
import client.core.scene.ApplicationScene;
import client.runtime.application.Application;
import java.time.Duration;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import sampleGame.battle.util.Camera;
import sampleGame.data.BattleData;
import sampleGame.data.entity.Entity;
import sampleGame.data.entity.component.KeyboardComponent;
import sampleGame.data.entity.components.*;
import sampleGame.data.tile.Tile;

public class OverworldScene extends ApplicationScene {
  @Getter @Setter private final Camera camera;

  @Getter @Setter private BattleData scene;

  private final SpriteRenderer spriteRenderer =
      new SpriteRenderer("shaders/sprite/SpriteVertex.glsl", "shaders/sprite/SpriteFragment.glsl");

  public OverworldScene(Application game) {
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
    //        var entity = new Entity();
    //        entity.addComponent(new PositionComponent(6, 6));
    //        entity.addComponent(new SpriteComponent("sprites/test"));
    //        entity.addComponent(new KinematicsComponent());
    //        entity.addComponent(new PlayerMovementComponent());
    //        entities.add(entity);

    // Box that we can Collide Into
    //        var box = new Entity();
    //        box.addComponent(new PositionComponent(8, 8));
    //        entities.add(box);

    //        scene = new BattleData(tiles, entities);

    var transform =
        Transform.orthographic(0, Application.SCREEN_WIDTH, Application.SCREEN_HEIGHT, 0, -1, 1);
    spriteRenderer.setProjection(transform);
  }

  @Override
  public void onEnter() {}

  @Override
  public void onUpdate(Duration delta) {
    for (var entity : scene.getEntities()) {
      if (!entity.hasComponent(KeyboardComponent.class)) continue;
      entity
          .getAllComponents(KeyboardComponent.class)
          .forEach(k -> k.onKeyboard(entity, application.getKeyboard()));
    }

    // find the player and make the camera follow them
    var player =
        scene.getEntities().stream()
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
