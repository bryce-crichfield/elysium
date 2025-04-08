package game.gui.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public interface GuiMouseHandler {
    static GuiMouseHandler onClick(Runnable action) {
        return new GuiMouseHandler() {
            @Override
            public void onMouseClicked(MouseEvent e) {
                action.run();
            }
        };
    }

    default void onMousePressed(MouseEvent e) {
    }

    default void onMouseReleased(MouseEvent e) {
    }

    default void onMouseClicked(MouseEvent e) {
    }

    default void onMouseDragged(MouseEvent e) {
    }

    default void onMouseMoved(MouseEvent e) {
    }

    default void onMouseWheelMoved(MouseWheelEvent e) {
    }

    default void onMouseEntered(MouseEvent e) {
    }

    default void onMouseExited(MouseEvent e) {
    }

    default void dispatchMouseEvent(MouseEvent e) {
        if (e.getID() == MouseEvent.MOUSE_PRESSED) {
            onMousePressed(e);
        } else if (e.getID() == MouseEvent.MOUSE_RELEASED) {
            onMouseReleased(e);
        } else if (e.getID() == MouseEvent.MOUSE_CLICKED) {
            onMouseClicked(e);
        } else if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
            onMouseDragged(e);
        } else if (e.getID() == MouseEvent.MOUSE_MOVED) {
            onMouseMoved(e);
        } else if (e.getID() == MouseEvent.MOUSE_WHEEL) {
            onMouseWheelMoved((MouseWheelEvent) e);
        } else if (e.getID() == MouseEvent.MOUSE_ENTERED) {
            onMouseEntered(e);
        } else if (e.getID() == MouseEvent.MOUSE_EXITED) {
            onMouseExited(e);
        }
    }
}
