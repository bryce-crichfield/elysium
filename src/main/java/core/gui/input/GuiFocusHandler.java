package core.gui.input;

@FunctionalInterface
public interface GuiFocusHandler {
    void onFocusChanged(boolean focused);
}
