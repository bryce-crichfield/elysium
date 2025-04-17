package game.state.battle.hud;

import game.gui.GuiScrollPanel;
import game.gui.control.GuiButton;
import game.gui.layout.GuiVerticalLayout;
import game.gui.style.GuiBackground;
import game.gui.style.GuiBorder;
import game.gui.style.GuiLabel;
import game.state.battle.BattleState;
import game.state.battle.Scene;
import game.state.battle.entity.Entity;
import game.state.battle.entity.components.PositionComponent;
import game.state.battle.entity.components.SpriteComponent;
import game.state.title.TitleState;
import game.transition.Transitions;
import game.util.Easing;

import java.awt.*;
import java.time.Duration;

public class EntityInspector extends GuiScrollPanel {
    private static final int WIDTH = 500;
    private static final int HEIGHT = 250;
    private final BattleState state;


    public EntityInspector(BattleState state, int x, int y) {
        super(x, y, WIDTH, HEIGHT);
        this.state = state;

        var layout = new GuiVerticalLayout();
        layout.setSpacing(5);
        layout.setPadding(5);
        this.setLayout(layout);

        var background = new GuiBackground.Fill(Color.BLACK);
        this.setBackground(background);

        var border = new GuiBorder(Color.WHITE, 2);
        this.setBorder(border);


    }

    @Override
    public void onUpdate(Duration delta) {
        super.onUpdate(delta);

        // clear the children
        this.getChildren().clear();

        int x = state.getCursor().getCursorX();
        int y = state.getCursor().getCursorY();
        var entity = state.getScene().findEntityByPosition(x, y);
        if (entity.isEmpty()) return;
        var selectedEntity = entity.get();
        System.out.println("Selected entity: " + selectedEntity);
        // add the entity's components to the inspector
        selectedEntity.getComponents().forEach((clazz, component) -> {
            var name = clazz.getSimpleName();
            var value = component.toString();
            var label = new GuiLabel(75, 20, name + ": " + value);
            addChild(label);
        });
    }
}
