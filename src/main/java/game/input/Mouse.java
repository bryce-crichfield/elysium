package game.input;

import game.event.Event;

import java.awt.event.*;

public class Mouse implements MouseListener, MouseMotionListener, MouseWheelListener {
    // Button constants
    public static final int LEFT = MouseEvent.BUTTON1;
    public static final int MIDDLE = MouseEvent.BUTTON2;
    public static final int RIGHT = MouseEvent.BUTTON3;

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
    private int x;
    private int y;

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

    public static MouseEvent translateEvent(MouseEvent e, int x, int y) {
        if (e instanceof MouseWheelEvent) {
            return new MouseWheelEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiersEx(),
                    x, y, e.getClickCount(), e.isPopupTrigger(), ((MouseWheelEvent) e).getScrollType(),
                    ((MouseWheelEvent) e).getScrollAmount(), ((MouseWheelEvent) e).getWheelRotation());
        } else {
            return new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiersEx(),
                    x, y, e.getClickCount(), e.isPopupTrigger(), e.getButton());
        }
    }

    // Method to receive transformed coordinates from Window
    public void setTransformedPosition(int gameX, int gameY) {
        this.x = gameX;
        this.y = gameY;
    }

    // State query methods
    public boolean anyPressed(int... buttonCodes) {
        for (int buttonCode : buttonCodes) {
            if (pressed(buttonCode)) {
                return true;
            }
        }
        return false;
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
    @Override
    public void mousePressed(MouseEvent e) {
        int button = e.getButton();
        boolean currentButton = newButtons[button];
        newButtons[button] = true;
        oldButtons[button] = currentButton;
        pressed.fire(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int button = e.getButton();
        boolean currentButton = newButtons[button];
        newButtons[button] = false;
        oldButtons[button] = currentButton;
        released.fire(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        clicked.fire(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // No special handling needed
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // No special handling needed
    }

    // MouseMotionListener implementation
    @Override
    public void mouseMoved(MouseEvent e) {
        moved.fire(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        dragged.fire(e);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        wheel.fire(e);
    }
}