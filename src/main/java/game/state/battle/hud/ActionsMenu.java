package game.state.battle.hud;

import game.gui.GuiContainer;
import game.gui.GuiScrollPanel;
import game.gui.control.GuiButton;
import game.gui.layout.GuiVerticalLayout;
import game.gui.style.GuiBackground;
import game.gui.style.GuiBorder;
import game.state.battle.BattleState;
import game.state.title.TitleState;
import game.transition.Transitions;
import game.util.Easing;

import java.awt.*;
import java.time.Duration;

public class ActionsMenu extends GuiContainer {
    private static final int WIDTH = 160;
    private static final int HEIGHT = 95;
    private final BattleState state;

    public ActionsMenu(BattleState state, int x, int y) {
        super(x, y, WIDTH, HEIGHT);
        this.state = state;

        var layout = new GuiVerticalLayout();
        layout.setSpacing(5);
        this.setLayout(layout);

        var background = new GuiBackground.Fill(Color.BLACK);
        this.setBackground(background);

        var border = new GuiBorder(Color.WHITE, 2);
        this.setBorder(border);

        var saveSceneBtn = new GuiButton("Save Scene", 100, 20, () -> {
            // Save scene logic
            System.out.println("Scene saved!");
        });

        var loadSceneBtn = new GuiButton("Load Scene", 100, 20, () -> {
            // Load scene logic
            System.out.println("Scene loaded!");
        });

        var exitBtn = new GuiButton("Exit", 100, 20, () -> {
            // Exit logic
            var transition = Transitions.fade(Duration.ofMillis(1000), Color.BLACK, Easing.cubicEaseIn());
            state.getGame().pushState(TitleState::new, transition);
        });

        this.addChild(saveSceneBtn);
        this.addChild(loadSceneBtn);
        this.addChild(exitBtn);
    }
}