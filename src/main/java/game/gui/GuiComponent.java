package game.gui;

import game.gui.input.GuiKeyHandler;
import game.gui.input.GuiMouseHandler;
import game.input.Mouse;
import lombok.Getter;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public abstract class GuiComponent {
    // Input handler delegation - keeps the flexibility
    protected final List<GuiMouseHandler> mouseHandlers = new ArrayList<>();
    protected final List<GuiKeyHandler> keyHandlers = new ArrayList<>();
    @Getter
    protected int x, y, width, height;
    @Getter
    protected boolean visible = true;
    @Getter
    protected boolean enabled = true;
    protected GuiComponent parent;

    public GuiComponent(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(Graphics2D g) {
        if (!visible) return;

        // Save transform state
        var originalTransform = g.getTransform();
        g.translate(x, y);

        // Custom rendering in subclasses
        onRender(g);

        // Restore transform
        g.setTransform(originalTransform);
    }

    protected void onRender(Graphics2D g) {
    }

    // Simple inheritance for updates
    public void update(Duration delta) {
        if (!visible) return;
        onUpdate(delta);
    }

    protected void onUpdate(Duration delta) {
        // Base implementation
    }

    public void addMouseHandler(GuiMouseHandler handler) {
        mouseHandlers.add(handler);
    }

    public void addKeyHandler(GuiKeyHandler handler) {
        keyHandlers.add(handler);
    }

    // Process mouse events and delegate to handlers
    public boolean processMouseEvent(MouseEvent e) {
        if (!visible || !enabled) return false;

        // Transform coordinates to local space
        Point localPoint = transformToLocalSpace(e.getPoint());

        // Check if point is within bounds
        if (!containsPoint(localPoint)) return false;

        // Move the event to local coordinates
        e = Mouse.translateEvent(e, localPoint.x, localPoint.y);

        // If the event gets handled by the component itself, return true
        if (onMouseEvent(e)) return true;

        // Delegate to handlers
        for (GuiMouseHandler handler : mouseHandlers) {
            if (e.isConsumed()) break;
            handler.dispatchMouseEvent(e);
        }

        return true;
    }

    protected boolean onMouseEvent(MouseEvent e) {
        return false;
    }

    protected Point transformToLocalSpace(Point point) {
        // Transform the point to local space
        return new Point(point.x - x, point.y - y);
    }

    protected boolean containsPoint(Point point) {
        return point.x >= 0 && point.x < width && point.y >= 0 && point.y < height;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    protected void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
