package game.gui.input;

import game.input.MouseEvent;

public interface GuiMouseHandler {
    static GuiMouseHandler onClick(Runnable action) {
        return new GuiMouseHandler() {
            @Override
            public GuiEventState onMouseClicked(MouseEvent.Clicked e) {
                action.run();
                return GuiEventState.CONSUMED;
            }
        };
    }

    default GuiEventState onMousePressed(MouseEvent.Pressed e) {
        return GuiEventState.NOT_CONSUMED;
    }

    default GuiEventState onMouseReleased(MouseEvent.Released e) {
        return GuiEventState.NOT_CONSUMED;
    }

    default GuiEventState onMouseClicked(MouseEvent.Clicked e) {
        return GuiEventState.NOT_CONSUMED;
    }

    default GuiEventState onMouseDragged(MouseEvent.Dragged e) {
        return GuiEventState.NOT_CONSUMED;
    }

    default GuiEventState onMouseMoved(MouseEvent.Moved e) {
        return GuiEventState.NOT_CONSUMED;
    }

    default GuiEventState onMouseWheelMoved(MouseEvent.WheelMoved e) {
        return GuiEventState.NOT_CONSUMED;
    }

    default GuiEventState dispatchMouseEvent(MouseEvent event) {
        // Type-safe dispatch using instanceof
        if (event instanceof MouseEvent.Moved moved) {
            return onMouseMoved(moved);
        } else if (event instanceof MouseEvent.Dragged dragged) {
            return onMouseDragged(dragged);
        } else if (event instanceof MouseEvent.Clicked clicked) {
            return onMouseClicked(clicked);
        } else if (event instanceof MouseEvent.Pressed pressed) {
            return onMousePressed(pressed);
        } else if (event instanceof MouseEvent.Released released) {
            return onMouseReleased(released);
        } else if (event instanceof MouseEvent.WheelMoved wheelMoved) {
            return onMouseWheelMoved(wheelMoved);
        }

        return GuiEventState.NOT_CONSUMED;
    }
}
