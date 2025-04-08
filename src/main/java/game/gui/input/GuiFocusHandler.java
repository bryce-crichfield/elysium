package game.gui.input;

@FunctionalInterface
public interface GuiFocusHandler {
    void onFocusChanged(boolean focused);
}
