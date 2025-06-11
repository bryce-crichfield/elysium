package sampleGame.battle;

import client.core.graphics.Renderer;
import client.core.graphics.Transform;
import client.core.graphics.background.Background;
import client.core.graphics.sprite.SpriteRenderer;
import client.core.gui.container.GuiContainer;
import client.core.gui.input.GuiEventState;
import client.core.gui.layout.GuiNullLayout;
import client.core.input.MouseEvent;
import client.core.scene.ApplicationScene;
import client.core.transition.Transitions;
import client.core.util.Easing;
import client.runtime.application.Application;
import client.runtime.system.networking.NetworkingSystem;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Function;
import lombok.Getter;
import sampleGame.battle.controller.BattleController;
import sampleGame.battle.controller.BattleControllerFactory;
import sampleGame.battle.controller.player.ObserverPlayerController;
import sampleGame.battle.hud.ActionsMenu;
import sampleGame.battle.hud.EntityInspector;
import sampleGame.battle.util.Camera;
import sampleGame.battle.util.Cursor;
import sampleGame.battle.util.Selection;
import sampleGame.data.BattleData;
import sampleGame.data.entity.Entity;
import sampleGame.data.entity.components.AnimationComponent;
import sampleGame.data.entity.components.PositionComponent;
import sampleGame.data.entity.components.SpriteComponent;
import sampleGame.data.entity.components.TileAnimationComponent;
import sampleGame.data.tile.Tile;
import sampleGame.server.BattleAction;
import sampleGame.title.TitleScene;

public class BattleScene extends ApplicationScene {
  @Getter private final Camera camera;
  @Getter private BattleData data;
  @Getter private final Cursor cursor;

  private final SpriteRenderer spriteRenderer =
      new SpriteRenderer("shaders/sprite/SpriteVertex.glsl", "shaders/sprite/SpriteFragment.glsl");

  @Getter private final Selection selection = new Selection();

  @Getter
  private final GuiContainer gui =
      new GuiContainer(0, 0, Application.SCREEN_WIDTH, Application.SCREEN_HEIGHT);

  @Getter private BattleController controller = new ObserverPlayerController(this);

  public BattleScene(Application game) {
    super(game);
    camera = new Camera(game);

    var tiles = new Tile[16][16];
    for (int x = 0; x < tiles.length; x++) {
      for (int y = 0; y < tiles[x].length; y++) {
        var tile = new Tile(x, y, "tiles/Cyan", true);
        tiles[x][y] = tile;
      }
    }

    var entityFactories = defineEntities();

    data = new BattleData(tiles, entityFactories);
    //        BattleData.serialize("scene1", data);
    //        scene = Scene.deserialize("scene1");
    cursor = new Cursor(game, this);
    addBackground(Background.stars());

    transitionTo(ObserverPlayerController::new);

    var transform =
        Transform.orthographic(0, Application.SCREEN_WIDTH, Application.SCREEN_HEIGHT, 0, -1, 1);
    spriteRenderer.setProjection(transform);

    gui.addChild(new ActionsMenu(this, 0, 0));
    gui.addChild(new EntityInspector(this, Application.SCREEN_WIDTH - 500, 0));
    gui.setLayout(new GuiNullLayout());
  }

  private static ArrayList<Function<String, Entity>> defineEntities() {
    var entityFactories = new ArrayList<Function<String, Entity>>();
    Function<String, Entity> entityFactory =
        (id) -> {
          var entity = new Entity(id);
          entity.addComponent(new PositionComponent(6, 6));
          entity.addComponent(new SpriteComponent("sprites/test"));
          entity.addComponent(new AnimationComponent());
          entity.addComponent(new TileAnimationComponent());
          return entity;
        };
    entityFactories.add(entityFactory);
    return entityFactories;
  }

  @Override
  public void onEnter() {
    //        application.getRuntimeContext().getSystem(NetworkingSystem.class).setHooks(new
    // BattleSceneHooks());
    application.getAudio().play("ambience/ambience_spacecraft_hold_loop", true, 0.25f);

    var reqId = UUID.randomUUID().toString();
    try {
      application
          .getRuntimeContext()
          .getSystem(NetworkingSystem.class)
          .ifPresent(
              networking -> {
                networking.callAsync(
                    "BattleService",
                    new BattleAction.GetState(),
                    response -> {
                      response
                          .result()
                          .forEach(
                              message -> System.out.println("Received battle state: " + message));
                    });
              });
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
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
      application.pushState(
          TitleScene::new,
          Transitions.fade(Duration.ofMillis(1000), Color.BLACK, Easing.cubicEaseIn()));
    }
  }

  @Override
  public void onUpdate(Duration delta) {
    var networking = application.getRuntimeContext().getSystem(NetworkingSystem.class);
    networking.ifPresent(
        net -> {
          var messages = net.getMessages();
          for (var message : messages) {
            //                System.out.println("BattleScene: Processing message: " + message);
          }

          var runnables = net.getQueue().getRunnables();
          for (var runnable : runnables) {
            try {
              runnable.run();
            } catch (Exception e) {
              //                    System.err.println("Error running networking callback: " +
              // e.getMessage());
            }
          }
        });

    gui.update(delta);
    data.onUpdate(delta);
    controller.onUpdate(delta);
  }

  @Override
  public void onRender(Renderer renderer) {
    // Get the camera worldTransform and render the world
    Transform worldTransform = camera.getTransform();
    renderer.pushTransform(worldTransform);
    spriteRenderer.setView(worldTransform);
    data.onRender(renderer, spriteRenderer);
    controller.onWorldRender(renderer);
    renderer.popTransform();

    gui.render(renderer);
  }

  public void transitionTo(BattleControllerFactory factory) {
    controller.onExit();
    controller = factory.create(this);
    controller.onEnter();
  }

  public void setBattleData(BattleData battleData) {
    System.out.println("this.data identity: " + System.identityHashCode(this.data));
    System.out.println("battleData identity: " + System.identityHashCode(battleData));

    if (this.data == battleData) {
      System.out.println("Same object reference - but forcing update anyway");
      // Force update even with same reference, since server state might have changed
      this.data = battleData;
      // Trigger any necessary UI updates here
      return;
    }

    System.out.println("Different objects - updating battle data");
    this.data = battleData;
  }
}
