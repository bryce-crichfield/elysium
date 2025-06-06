package game.gui.manager;

import game.gui.GuiComponent;
import game.gui.input.GuiEventState;
import game.input.MouseEvent;
import lombok.Getter;

public class GuiMouseCaptureManager {
    @Getter
    private static final GuiMouseCaptureManager instance = new GuiMouseCaptureManager();

    @Getter
    private GuiComponent capturedComponent = null;

    public boolean isCapturedComponent(GuiComponent component) {
        if (capturedComponent == null) {
            return false;
        }

        return capturedComponent.equals(component);
    }

    public boolean lacksCapturedComponent() {
        return capturedComponent == null;
    }

    public void setMouseCapture(GuiComponent component) {
        capturedComponent = component;
    }

    public void releaseMouseCapture() {
        capturedComponent = null;
    }

    public GuiEventState dispatchToCapturedComponent(MouseEvent e) {
//        boolean isCapturedEvent = (e instanceof MouseEvent.Dragged || e instanceof MouseEvent.Released);
        if (capturedComponent != null) {
            // Send directly to captured component
            return capturedComponent.processMouseEvent(e);
        }

        return GuiEventState.NOT_CONSUMED;
    }
}
