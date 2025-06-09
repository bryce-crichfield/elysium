package client.core.gui.input;

public interface GuiKeyHandler {
    default void onKeyPressed(int keyCode) {
    }

    default void onKeyReleased(int keyCode) {
    }

    default void onKeyTyped(char keyChar) {
    }
}
