package game.gui.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public interface GuiMouseHandler {
    default void onMousePressed(MouseEvent e) {}
    default void onMouseReleased(MouseEvent e) {}
    default void onMouseClicked(MouseEvent e) {}
    default void onMouseDragged(MouseEvent e) {}
    default void onMouseMoved(MouseEvent e) {}
    default void onMouseWheelMoved(MouseWheelEvent e) {}
    default void onMouseEntered(MouseEvent e) {}
    default void onMouseExited(MouseEvent e) {}

    static GuiMouseHandler onClick(Runnable action) {
        return new GuiMouseHandler() {
            @Override
            public void onMouseClicked(MouseEvent e) {
                action.run();
            }
        };
    }
}
