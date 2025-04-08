package game.gui.input;

import game.gui.GuiComponent;

import java.awt.event.MouseEvent;

public class GuiMouseManager {
    private static GuiComponent capturedComponent = null;

    public static boolean isCapturedComponent(GuiComponent component) {
        if (capturedComponent == null) {
            return false;
        }

        return capturedComponent.equals(component);
    }

    public static void setMouseCapture(GuiComponent component) {
        capturedComponent = component;
    }

    public static void releaseMouseCapture() {
        capturedComponent = null;
    }

    public static boolean dispatchToCapturedComponent(MouseEvent e) {
        boolean isCapturedEvent = (e.getID() == MouseEvent.MOUSE_DRAGGED || e.getID() == MouseEvent.MOUSE_RELEASED);
        if (capturedComponent != null && isCapturedEvent) {
            System.out.println("Dispatching event to captured component: " + e);
            // Send directly to captured component
            capturedComponent.processMouseEvent(e);

            // Release capture when mouse is released
            if (e.getID() == MouseEvent.MOUSE_RELEASED) {
                releaseMouseCapture();
            }

            return true; // event was handled by the captured component
        }

        return false; // event was not handled a captured component
    }
}
