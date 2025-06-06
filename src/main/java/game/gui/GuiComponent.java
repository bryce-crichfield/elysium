package game.gui;

import game.graphics.Renderer;
import game.graphics.Transform;
import game.gui.input.*;
import game.gui.manager.GuiFocusManager;
import game.gui.manager.GuiHoverManager;
import game.gui.manager.GuiMouseCaptureManager;
import game.gui.style.GuiStyle;
import game.input.MouseEvent;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class GuiComponent {
    protected final List<GuiMouseHandler> mouseHandlers = new ArrayList<>();
    protected final List<GuiKeyHandler> keyHandlers = new ArrayList<>();
    protected final List<GuiFocusHandler> focusHandlers = new ArrayList<>();
    protected final List<GuiHoverHandler> hoverHandlers = new ArrayList<>();
    @Getter
    protected int x, y, width, height;
    @Getter
    @Setter
    protected boolean isVisible = true;
    @Getter
    protected boolean isEnabled = true;
    @Getter
    @Setter
    protected boolean isFocused = true;
    @Getter
    protected boolean isHovered = false;

    @Setter
    protected GuiComponent parent;                                      // Only set this if you know what you're doing

    @Getter
    @Setter
    protected GuiStyle style;

    public GuiComponent(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(Renderer renderer) {
        if (!isVisible) return;

        renderer.pushTransform(Transform.createTranslate(x, y));
        onRender(renderer);
        renderer.popTransform();
    }

    protected void onRender(Renderer renderer) {
    }

    // Simple inheritance for updates
    public void update(Duration delta) {
        if (!isVisible) return;
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
    public GuiEventState processMouseEvent(MouseEvent event) {
        if (!isVisible || !isEnabled) return GuiEventState.NOT_CONSUMED;

        // Transform coordinates to local space
        Point point = event.getPoint();
        // Transform the point to local space
        Point localPoint = new Point(point.x - x, point.y - y);
        boolean isInBounds = containsPoint(localPoint);

        // Move the event to local coordinates
//        var e = Mouse.translateEvent(event, localPoint.x, localPoint.y);
        var e = event.withPoint(localPoint);

        // Check for hover events
        boolean mouseEntered = e instanceof MouseEvent.Moved && (!isHovered && isInBounds);
        boolean mouseExited = e instanceof MouseEvent.Moved && (isHovered && !isInBounds);
        isHovered = isInBounds;

        if (mouseEntered && GuiMouseCaptureManager.getInstance().lacksCapturedComponent()) {
            // HOW DOES THIS WORK!?!?!?
            System.out.println("Capture Mouse Component: " + GuiMouseCaptureManager.getInstance().getCapturedComponent());
            GuiHoverManager.getInstance().enter(e, this);
        }

        if (mouseExited && GuiMouseCaptureManager.getInstance().lacksCapturedComponent()) {
            // HOW DOES THIS WORK!?!?!?
            GuiHoverManager.getInstance().exit(e, this);
        }

        // Check if point is within bounds for other events
        if (!isInBounds && !GuiMouseCaptureManager.getInstance().isCapturedComponent(this)) return GuiEventState.NOT_CONSUMED;

        // Set focus on mouse click
        if (e instanceof MouseEvent.Pressed && isInBounds) {
            GuiFocusManager.getInstance().setFocus(this);
        }

        // If the event gets handled by the component itself, return true
        if (onMouseEvent(e) == GuiEventState.CONSUMED) return GuiEventState.CONSUMED;

        // Delegate to handlers
        for (GuiMouseHandler handler : mouseHandlers) {
            if (e.isConsumed()) break;
            if (handler.dispatchMouseEvent(e) == GuiEventState.CONSUMED) return GuiEventState.CONSUMED;
        }

        return GuiEventState.NOT_CONSUMED;
    }

    protected GuiEventState onMouseEvent(MouseEvent e) {
        return GuiEventState.NOT_CONSUMED;
    }

    protected boolean containsPoint(Point point) {
        return point.x >= 0 && point.x < width && point.y >= 0 && point.y < height;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int width, int height) {
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

    protected abstract String getComponentName();

    public void applyStyle(Map<String, GuiStyle> styles) {
        GuiStyle style = styles.get(getComponentName());
        if (style != null) {
            setStyle(style);
        }
    }
}
