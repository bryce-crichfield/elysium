package game.input;

import game.input.MouseEvent;
import game.event.Event;


public class Mouse  {
    // Button constants
    public static final int LEFT = 1;
    public static final int MIDDLE = 2;
    public static final int RIGHT = 3;

    // Events
    public static final Event<MouseEvent> pressed = new Event<>();
    public static final Event<MouseEvent> released = new Event<>();
    public static final Event<MouseEvent> clicked = new Event<>();
    public static final Event<MouseEvent> moved = new Event<>();
    public static final Event<MouseEvent> dragged = new Event<>();
    public static final Event<MouseEvent> wheel = new Event<>();

    // State tracking
    private final boolean[] oldButtons;
    private final boolean[] newButtons;
    private final int x;
    private final int y;

    public Mouse() {
        oldButtons = new boolean[4]; // Buttons are 1-indexed in MouseEvent
        newButtons = new boolean[4];

        for (int i = 0; i < 4; i++) {
            oldButtons[i] = false;
            newButtons[i] = false;
        }

        x = 0;
        y = 0;
    }

    public boolean pressed(int buttonCode) {
        return newButtons[buttonCode] && !oldButtons[buttonCode];
    }

    public boolean held(int buttonCode) {
        return newButtons[buttonCode] && oldButtons[buttonCode];
    }

    public boolean down(int buttonCode) {
        return newButtons[buttonCode] || oldButtons[buttonCode];
    }

    public boolean released(int buttonCode) {
        return !newButtons[buttonCode] && oldButtons[buttonCode];
    }

    // Position getters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * Check if the mouse is within the given rectangle in game coordinates
     */
    public boolean isOver(int rectX, int rectY, int width, int height) {
        return x >= rectX && x < rectX + width && y >= rectY && y < rectY + height;
    }

    // Update method to be called once per frame
    public void onUpdate() {
        System.arraycopy(newButtons, 0, oldButtons, 0, 4);
    }

    // MouseListener implementation
    public void mousePressed(MouseEvent.Pressed e) {
        int button = e.getButton();
        boolean currentButton = newButtons[button];
        newButtons[button] = true;
        oldButtons[button] = currentButton;
        pressed.fire(e);
    }

    public void mouseReleased(MouseEvent.Released e) {
        int button = e.getButton();
        boolean currentButton = newButtons[button];
        newButtons[button] = false;
        oldButtons[button] = currentButton;
        released.fire(e);
    }

    public void mouseClicked(MouseEvent.Clicked e) {
        clicked.fire(e);
    }

    // MouseMotionListener implementation
    public void mouseMoved(MouseEvent.Moved e) {
        moved.fire(e);
    }

    public void mouseDragged(MouseEvent.Dragged e) {
        dragged.fire(e);
    }

    public void mouseWheelMoved(MouseEvent.WheelMoved e) {
        wheel.fire(e);
    }
}