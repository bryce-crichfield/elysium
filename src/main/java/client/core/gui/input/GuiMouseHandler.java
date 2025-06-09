package client.core.gui.input;

import client.core.input.MouseEvent;

public abstract class GuiMouseHandler {
    public static GuiMouseHandler onClick(Runnable action) {
        return new GuiMouseHandler() {
            @Override
            public GuiEventState onMouseClicked(MouseEvent.Clicked e) {
                action.run();
                return GuiEventState.CONSUMED;
            }
        };
    }

    public GuiEventState onMousePressed(MouseEvent.Pressed e) {
        return GuiEventState.NOT_CONSUMED;
    }

    public GuiEventState onMouseReleased(MouseEvent.Released e) {
        return GuiEventState.NOT_CONSUMED;
    }

    public GuiEventState onMouseClicked(MouseEvent.Clicked e) {
        return GuiEventState.NOT_CONSUMED;
    }

    public GuiEventState onMouseDragged(MouseEvent.Dragged e) {
        return GuiEventState.NOT_CONSUMED;
    }

    public GuiEventState onMouseMoved(MouseEvent.Moved e) {
        return GuiEventState.NOT_CONSUMED;
    }

    public GuiEventState onMouseWheelMoved(MouseEvent.WheelMoved e) {
        return GuiEventState.NOT_CONSUMED;
    }

    public final GuiEventState dispatchMouseEvent(MouseEvent event) {
        switch (event) {
            case MouseEvent.Moved moved -> {
                return onMouseMoved(moved);
            }
            case MouseEvent.Dragged dragged -> {
                return onMouseDragged(dragged);
            }
            case MouseEvent.Clicked clicked -> {
                return onMouseClicked(clicked);
            }
            case MouseEvent.Pressed pressed -> {
                return onMousePressed(pressed);
            }
            case MouseEvent.Released released -> {
                return onMouseReleased(released);
            }
            case MouseEvent.WheelMoved wheelMoved -> {
                return onMouseWheelMoved(wheelMoved);
            }
            default -> {
                return GuiEventState.NOT_CONSUMED;
            }
        }
    }
}
