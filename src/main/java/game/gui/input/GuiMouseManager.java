package game.gui.input;

import game.gui.GuiComponent;
import game.input.MouseEvent;

public class GuiMouseManager {
    private static GuiComponent capturedComponent = null;

    public static boolean isCapturedComponent(GuiComponent component) {
        if (capturedComponent == null) {
            return false;
        }

        return capturedComponent.equals(component);
    }

    public static boolean hasCapturedComponent() {
        return capturedComponent != null;
    }

    public static void setMouseCapture(GuiComponent component) {
        capturedComponent = component;
    }

    public static void releaseMouseCapture() {
        capturedComponent = null;
    }

    public static boolean dispatchToCapturedComponent(MouseEvent e) {
        boolean isCapturedEvent = (e instanceof MouseEvent.Dragged || e instanceof MouseEvent.Released);
        if (capturedComponent != null && isCapturedEvent) {
            // Send directly to captured component
            capturedComponent.processMouseEvent(e);

            // Release capture when mouse is released
            if (e instanceof MouseEvent.Released) {
                releaseMouseCapture();
            }

            return true; // event was handled by the captured component
        }

        return false; // event was not handled a captured component
    }
}
