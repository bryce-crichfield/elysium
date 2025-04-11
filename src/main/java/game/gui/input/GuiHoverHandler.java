package game.gui.input;

import game.input.MouseEvent;

public interface GuiHoverHandler {
    void onEnter(MouseEvent event);
    void onExit(MouseEvent event);
}
