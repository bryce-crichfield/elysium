package game.input;

import lombok.Value;

@Value
public class KeyEvent {
    public static final int VK_W = 87;
    public static final int VK_A = 65;
    public static final int VK_S = 83;
    public static final int VK_D = 68;
    public static final int VK_SPACE = 32;
    public static final int VK_SHIFT = 16;
    public static final int VK_ESCAPE = 27;
    public static final int VK_ENTER = 10;
    public static final int VK_UP = 38;
    public static final int VK_DOWN = 40;
    public static final int VK_LEFT = 37;
    public static final int VK_RIGHT = 39;

    int keyCode;
    int action;

    public static KeyEvent fromAwt(java.awt.event.KeyEvent e) {
        return new KeyEvent(e.getKeyCode(), e.getID());
    }
}
