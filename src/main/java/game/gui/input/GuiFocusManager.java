package game.gui.input;

import game.gui.GuiComponent;
import lombok.Getter;

public class GuiFocusManager {
    @Getter
    private static GuiFocusManager instance = new GuiFocusManager();

    private GuiComponent focusedComponent = null;

    public void setFocus(GuiComponent component) {
        if (focusedComponent == component) return;

        // Remove focus from previous component
        if (focusedComponent != null) {
            focusedComponent.setFocused(false);
        }

        // Set focus to new component
        focusedComponent = component;
        if (component != null) {
            component.setFocused(true);
        }
    }

    public GuiComponent getFocusedComponent() {
        return focusedComponent;
    }

    public void clearFocus() {
        setFocus(null);
    }

    // Add method for tab traversal between components
    public void moveFocusForward() {
        // Implementation to find next focusable component
    }

    public void moveFocusBackward() {
        // Implementation to find previous focusable component
    }
}
