package game.battle.hud;

import core.gui.container.GuiScrollPanel;
import core.gui.control.GuiButton;
import core.gui.layout.GuiVerticalLayout;
import core.gui.style.GuiBackground;
import core.gui.style.GuiBorder;
import core.gui.style.GuiStyle;
import game.battle.BattleState;
import core.util.WatchedFile;

import java.awt.*;
import java.time.Duration;

public class SelectEntityAction extends GuiScrollPanel {
    private static final int WIDTH = 115;
    private static final int HEIGHT = 250;

    private final BattleState state;
    private final WatchedFile stylesheet = new WatchedFile("styles/SelectEntityAction.json");

    public SelectEntityAction(BattleState state, int x, int y) {
        super(x, y, WIDTH, HEIGHT);
        this.state = state;

        var layout = new GuiVerticalLayout();
        layout.setSpacing(5);
        layout.setPadding(5);
        this.setLayout(layout);

        var background = new GuiBackground.Fill(new Color(0, 0, 0, 0.5f));

        var border = new GuiBorder(Color.WHITE, 2);

        var moveBtn = new GuiButton("Move", 65, 20, () -> {
        });

        var attackBtn = new GuiButton("Attack", 65, 20, () -> {
        });

        var itemBtn = new GuiButton("Item", 65, 20, () -> {
        });

        var abilityBtn = new GuiButton("Ability", 65, 20, () -> {
        });

        var backBtn = new GuiButton("Back", 65, 20, () -> {
        });

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
