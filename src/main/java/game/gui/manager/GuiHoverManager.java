package game.gui.manager;

import game.gui.GuiComponent;
import lombok.Getter;

import game.input.MouseEvent;

public class GuiHoverManager {
    @Getter
    private static final GuiHoverManager instance = new GuiHoverManager();

    private GuiComponent hoveredComponent = null;

    public void enter(MouseEvent event, GuiComponent component) {
        if (hoveredComponent == null) {
            hoveredComponent = component;
            hoveredComponent.dispatchOnEnter(event);
        }

        if (hoveredComponent != null && hoveredComponent != component) {
            hoveredComponent.dispatchOnExit(event);
            hoveredComponent = component;
            hoveredComponent.dispatchOnEnter(event);
        }
    }

    public void exit(MouseEvent event, GuiComponent component) {
        if (hoveredComponent != null && hoveredComponent == component) {
            hoveredComponent.dispatchOnExit(event);
            hoveredComponent = null;
        }
    }

    public void clear() {
        if (hoveredComponent != null) {
            hoveredComponent.dispatchOnExit(null);
            hoveredComponent = null;
        }
    }
}
