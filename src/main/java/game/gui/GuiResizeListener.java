package game.gui;

interface GuiResizeListener {
    void onResize(GuiElement element, int oldWidth, int oldHeight, int newWidth, int newHeight);
}
