package game.battle.hud;

import core.gui.container.GuiScrollPanel;
import core.gui.layout.GuiVerticalLayout;
import core.gui.control.GuiLabel;
import game.battle.BattleState;

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
