package sampleGame.battle.hud;

import client.core.gui.container.GuiScrollPanel;
import client.core.gui.control.GuiButton;
import client.core.gui.layout.GuiVerticalLayout;
import client.core.gui.style.GuiBackground;
import client.core.gui.style.GuiBorder;
import client.core.gui.style.GuiStyle;
import client.core.util.WatchedFile;
import java.awt.*;
import java.time.Duration;
import sampleGame.battle.BattleScene;

public class SelectEntityAction extends GuiScrollPanel {
  private static final int WIDTH = 115;
  private static final int HEIGHT = 250;

  private final BattleScene state;
  private final WatchedFile stylesheet = new WatchedFile("styles/SelectEntityAction.json");

  public SelectEntityAction(BattleScene state, int x, int y) {
    super(x, y, WIDTH, HEIGHT);
    this.state = state;

    var layout = new GuiVerticalLayout();
    layout.setSpacing(5);
    layout.setPadding(5);
    this.setLayout(layout);

    var background = new GuiBackground.Fill(new Color(0, 0, 0, 0.5f));

    var border = new GuiBorder(Color.WHITE, 2);

    var moveBtn = new GuiButton("Move", 65, 20, () -> {});

    var attackBtn = new GuiButton("Attack", 65, 20, () -> {});

    var itemBtn = new GuiButton("Item", 65, 20, () -> {});

    var abilityBtn = new GuiButton("Ability", 65, 20, () -> {});

    var backBtn = new GuiButton("Back", 65, 20, () -> {});

    this.addChild(moveBtn);
    this.addChild(attackBtn);
    this.addChild(itemBtn);
    this.addChild(abilityBtn);
    this.addChild(backBtn);

    this.setVisible(false);

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
