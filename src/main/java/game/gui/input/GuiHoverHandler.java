package game.gui.input;

import java.awt.event.MouseEvent;

public interface GuiHoverHandler {
    void onEnter(MouseEvent event);
    void onExit(MouseEvent event);
}
