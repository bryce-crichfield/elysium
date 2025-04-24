package game.gui.input;

import game.gui.GuiComponent;
import game.input.MouseEvent;
import lombok.Getter;

public class GuiMouseCapture {
    @Getter
    private static GuiComponent capturedComponent = null;

    public static boolean isCapturedComponent(GuiComponent component) {
        if (capturedComponent == null) {
            return false;
        }

        return capturedComponent.equals(component);
    }

    public static boolean lacksCapturedComponent() {
        return capturedComponent == null;
    }

    public static void setMouseCapture(GuiComponent component) {
        capturedComponent = component;
    }

    public static void releaseMouseCapture() {
        capturedComponent = null;
    }

    public static GuiEventState dispatchToCapturedComponent(MouseEvent e) {
//        boolean isCapturedEvent = (e instanceof MouseEvent.Dragged || e instanceof MouseEvent.Released);
        if (capturedComponent != null) {
            // Send directly to captured component
            return capturedComponent.processMouseEvent(e);
        }

        return GuiEventState.NOT_CONSUMED;
    }
}
