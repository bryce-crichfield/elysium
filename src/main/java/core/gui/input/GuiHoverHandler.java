package core.gui.input;

import core.input.MouseEvent;

public interface GuiHoverHandler {
    void onEnter(MouseEvent event);
    void onExit(MouseEvent event);
}
