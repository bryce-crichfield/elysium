package client.core.gui.input;

import client.core.input.MouseEvent;

public interface GuiHoverHandler {
  void onEnter(MouseEvent event);

  void onExit(MouseEvent event);
}
