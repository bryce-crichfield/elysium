package sampleGame.battle.hud;

import client.core.gui.container.GuiScrollPanel;
import client.core.gui.control.GuiButton;
import client.core.gui.layout.GuiVerticalLayout;
import client.core.gui.style.GuiBackground;
import client.core.gui.style.GuiBorder;
import client.core.gui.style.GuiStyle;
import client.core.transition.Transitions;
import client.core.util.Easing;
import client.core.util.WatchedFile;
import java.awt.*;
import java.time.Duration;
import java.util.UUID;
import sampleGame.battle.BattleScene;
import sampleGame.data.BattleData;
import sampleGame.data.entity.Entity;
import sampleGame.data.entity.components.PositionComponent;
import sampleGame.data.entity.components.SpriteComponent;
import sampleGame.data.entity.entities.Fireball;
import sampleGame.title.TitleScene;

public class ActionsMenu extends GuiScrollPanel {
  private static final int WIDTH = 115;
  private static final int HEIGHT = 250;
  private final BattleScene state;
  private final WatchedFile stylesheet = new WatchedFile("styles/ActionMenu.json");

  public ActionsMenu(BattleScene state, int x, int y) {
    super(x, y, WIDTH, HEIGHT);
    this.state = state;

    var layout = new GuiVerticalLayout();
    layout.setSpacing(5);
    layout.setPadding(5);
    this.setLayout(layout);

    var background = new GuiBackground.Fill(new Color(0, 0, 0, 0.5f));

    var border = new GuiBorder(Color.WHITE, 2);

    var saveSceneBtn =
        new GuiButton(
            "Save Scene",
            100,
            20,
            () -> {
              // Save scene logic
              BattleData.serialize("scene1", state.getData());
            });

    var createEntity =
        new GuiButton(
            "Create",
            100,
            20,
            () -> {
              // Load scene logic
              var cursorX = state.getCursor().getCursorX();
              var cursorY = state.getCursor().getCursorY();
              var entity = new Entity(UUID.randomUUID().toString());
              var position = new PositionComponent(cursorX, cursorY);
              var sprite = new SpriteComponent("sprites/test");
              entity.addComponent(position);
              entity.addComponent(sprite);
              state.getData().addEntity(entity);
            });

    var removeEntity =
        new GuiButton(
            "Remove",
            100,
            20,
            () -> {
              var cursorX = state.getCursor().getCursorX();
              var cursorY = state.getCursor().getCursorY();
              var entity = state.getData().findEntityByPosition(cursorX, cursorY);
              entity.ifPresent(value -> state.getData().removeEntity(value));
            });

    var spawnFireball =
        new GuiButton(
            "Fireball",
            100,
            20,
            () -> {
              var cursorX = state.getCursor().getCursorX();
              var cursorY = state.getCursor().getCursorY();
              var fireball = Fireball.create(cursorX, cursorY);
              state.getData().addEntity(fireball);
            });

    var exitBtn =
        new GuiButton(
            "Exit",
            100,
            20,
            () -> {
              // Exit logic
              var transition =
                  Transitions.fade(Duration.ofMillis(1000), Color.BLACK, Easing.cubicEaseIn());
              state.getApplication().pushState(TitleScene::new, transition);
            });

    this.addChild(saveSceneBtn);
    this.addChild(createEntity);
    this.addChild(removeEntity);
    this.addChild(spawnFireball);
    this.addChild(exitBtn);

    var style = GuiStyle.load(stylesheet.getAbsolutePath().toString());
    this.applyStyle(style);
  }

  @Override
  protected void onUpdate(Duration delta) {
    super.onUpdate(delta);

    if (stylesheet.hasChanged()) {
      var style = GuiStyle.load(stylesheet.getAbsolutePath().toString());
      this.applyStyle(style);
    }
  }
}
