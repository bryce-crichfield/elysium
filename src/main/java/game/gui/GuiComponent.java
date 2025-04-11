package game.gui;

import game.gui.input.*;
import game.input.Mouse;
import game.input.MouseEvent;
import game.platform.Renderer;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public abstract class GuiComponent {
    // Input handler delegation - keeps the flexibility
    protected final List<GuiMouseHandler> mouseHandlers = new ArrayList<>();
    protected final List<GuiKeyHandler> keyHandlers = new ArrayList<>();
    protected final List<GuiFocusHandler> focusHandlers = new ArrayList<>();
    protected final List<GuiHoverHandler> hoverHandlers = new ArrayList<>();
    @Getter
    protected int x, y, width, height;
    @Getter
    protected boolean visible = true;
    @Getter
    protected boolean enabled = true;
    @Getter
    @Setter
    protected boolean isFocused = true;
    @Getter
    protected boolean isHovered = false;

    protected GuiComponent parent;

    public GuiComponent(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(Renderer renderer) {
        if (!visible) return;

        // Save transform state
        var originalTransform = renderer.getTransform();
        renderer.translate(x, y);

        // Custom rendering in subclasses
        onRender(renderer);

        // Restore transform
        renderer.setTransform(originalTransform);
    }

    protected void onRender(Renderer renderer) {
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
    public boolean processMouseEvent(MouseEvent event) {
        if (!visible || !enabled) return false;

        // Transform coordinates to local space
        Point localPoint = transformToLocalSpace(event.getPoint());
        boolean isInBounds = containsPoint(localPoint);

        // Move the event to local coordinates
//        var e = Mouse.translateEvent(event, localPoint.x, localPoint.y);
        var e = event.withPoint(localPoint);

        // Check for hover events
        boolean mouseEntered = e instanceof MouseEvent.Moved && (!isHovered && isInBounds);
        boolean mouseExited = e instanceof MouseEvent.Moved && (isHovered && !isInBounds);
        isHovered = isInBounds;

        if (mouseEntered && !GuiMouseManager.hasCapturedComponent()) {
            // HOW DOES THIS WORK!?!?!?
            GuiHoverManager.getInstance().enter(e, this);
        }

        if (mouseExited && !GuiMouseManager.hasCapturedComponent()) {
            // HOW DOES THIS WORK!?!?!?
            GuiHoverManager.getInstance().exit(e, this);
        }

        // Check if point is within bounds for other events
        if (!isInBounds && !GuiMouseManager.isCapturedComponent(this)) return false;

        // Set focus on mouse click
        if (e instanceof MouseEvent.Pressed && isInBounds) {
            GuiFocusManager.getInstance().setFocus(this);
        }

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

    protected void addHoverHandler(GuiHoverHandler guiHoverHandler) {
        hoverHandlers.add(guiHoverHandler);
    }

    public final void dispatchOnEnter(MouseEvent event) {
        isHovered = true;
        hoverHandlers.forEach(h -> h.onEnter(event));
    }

    public final void dispatchOnExit(MouseEvent event) {
        isHovered = false;
        hoverHandlers.forEach(h -> h.onExit(event));
    }
}
