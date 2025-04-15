package game.gui.input;

import game.input.MouseEvent;

public interface GuiMouseHandler {
    static GuiMouseHandler onClick(Runnable action) {
        return new GuiMouseHandler() {
            @Override
            public void onMouseClicked(MouseEvent.Clicked e) {
                action.run();
            }
        };
    }

    default void onMousePressed(MouseEvent.Pressed e) {
    }

    default void onMouseReleased(MouseEvent.Released e) {
    }

    default void onMouseClicked(MouseEvent.Clicked e) {
    }

    default void onMouseDragged(MouseEvent.Dragged e) {
    }

    default void onMouseMoved(MouseEvent.Moved e) {
    }

    default void onMouseWheelMoved(MouseEvent.WheelMoved e) {
    }

    default void dispatchMouseEvent(MouseEvent event) {
        // Type-safe dispatch using instanceof
        if (event instanceof MouseEvent.Moved moved) {
            onMouseMoved(moved);
        } else if (event instanceof MouseEvent.Dragged dragged) {
            onMouseDragged(dragged);
        } else if (event instanceof MouseEvent.Clicked clicked) {
            onMouseClicked(clicked);
        } else if (event instanceof MouseEvent.Pressed pressed) {
            onMousePressed(pressed);
        } else if (event instanceof MouseEvent.Released released) {
            onMouseReleased(released);
        } else if (event instanceof MouseEvent.WheelMoved wheelMoved) {
            onMouseWheelMoved(wheelMoved);
        }
    }
}
