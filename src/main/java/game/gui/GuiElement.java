package game.gui;

import game.gui.input.GuiKeyHandler;
import game.gui.input.GuiMouseHandler;
import game.gui.interfaces.*;
import game.gui.layout.GuiLayout;
import game.gui.scroll.GuiScrollManager;
import game.gui.style.*;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;


public class GuiElement implements GuiUpdatable,
        GuiRenderable,
        GuiFocusable,
        GuiHoverable,
        GuiKeyHandler,
        GuiMouseHandler {
    @Getter
    private int x, y, width, height;

    private GuiElement parent;
    private final List<GuiElement> children = new ArrayList<>();

    private GuiLayout layoutManager;

    private GuiUpdatable guiUpdateHandler;
    protected GuiRenderable guiRenderHandler;
    private GuiFocusable guiFocusHandler;
    private GuiHoverable guiHoverHandler;
    private GuiKeyHandler guiKeyHandler;
    @Setter
    private GuiMouseHandler guiMouseHandler;

    @Setter
    private int cornerRadius = 0;
    @Setter
    private GuiBackground background;
    @Setter
    private GuiBorder border;

    @Getter
    private boolean isEnabled = true;
    @Getter
    private boolean isVisible = true;
    private boolean isFocused = false;

    private GuiScrollManager scrollManager;
    private boolean isOverflowEnabled = false;

    public GuiElement(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setLayout(GuiLayout layout) {
        this.layoutManager = layout;
        if (layoutManager != null) {
            layoutManager.onLayout(this);
        }
    }

    public void setScrollManager(GuiScrollManager scrollManager) {
        this.scrollManager = scrollManager;
        if (scrollManager != null) {
            scrollManager.setViewportSize(width, height);
            scrollManager.onViewportLayoutChanged();
        }
        performLayout();
    }

    // Update ========================================================================================================
    @Override
    public final void onUpdate(Duration delta) {
        if (!isVisible) return;

        if (guiUpdateHandler != null) {
            guiUpdateHandler.onUpdate(delta);
        }

        for (var child : children) {
            child.onUpdate(delta);
        }
    }

    // Rendering =======================================================================================================
    @Override
    public final void onRender(Graphics2D g) {
        if (!isVisible) return;

        // Save the original transform and clip
        AffineTransform originalTransform = g.getTransform();

        try {
            // Move to this component's coordinate space
            g.translate(x, y);

            // If scrolling is enabled, apply scroll offset
            if (scrollManager != null) {
                g.translate(-scrollManager.getScrollXOffset(), -scrollManager.getScrollYOffset());
            }

            // Apply clipping - this restricts drawing to this component's bounds
            // while respecting any parent clipping
            Shape savedClip = setClip(g);

            // Render background
            int bgWidth = scrollManager != null ? scrollManager.getViewportWidth() : width;
            int bgHeight = scrollManager != null ? scrollManager.getContentHeight() : height;

            if (background != null) {
                background.onRender(g, bgWidth, bgHeight, cornerRadius);
            }

            if (border != null) {
                border.render(g, bgWidth, bgHeight, cornerRadius);
            }

            // Custom rendering for this component
            if (guiRenderHandler != null) {
                guiRenderHandler.onRender(g);
            }

            // Render children - they inherit the clip we've set
            for (var child : children) {
                if (child.isVisible) {
                    child.onRender(g);
                }
            }

            // Render scrollbars if needed
            if (scrollManager != null) {
                // Reset translation for scrollbars (they should be in component space)
                g.translate(scrollManager.getScrollXOffset(), scrollManager.getScrollYOffset());

                // Render scrollbars based on scrollManager settings
                renderScrollbars(g);

                // Restore content translation
                g.translate(-scrollManager.getScrollXOffset(), -scrollManager.getScrollYOffset());
            }

            // Restore the original clip
            g.setClip(savedClip);
        } finally {
            // Always restore the original transform, even if an exception occurs
            g.setTransform(originalTransform);
        }
    }

    private Shape setClip(Graphics2D g) {
        // Save the original clip
        Shape originalClip = g.getClip();

        // If overflow is allowed, don't modify the clip
        if (isOverflowEnabled) {
            return originalClip;
        }

        // Create a local rectangle representing this component's viewport
        Rectangle localBounds = new Rectangle(0, 0, width, height);

        // If scrolling is enabled, adjust clip for visible portion only
        if (scrollManager != null && scrollManager.shouldClipContent()) {
            Rectangle visibleContent = scrollManager.getVisibleContentBounds();
            // Convert to local coordinates if needed
            localBounds = visibleContent;
        }

        // If we are nested within another component, intersect with its clip
        if (originalClip != null) {
            // Intersect with the existing clip...
            Area clipArea = new Area(originalClip);
            Area componentArea = new Area(localBounds);
            clipArea.intersect(componentArea);
            g.setClip(clipArea);
        } else {
            g.setClip(localBounds);
        }

        return originalClip;
    }

    private void renderScrollbars(Graphics2D g) {
        if (scrollManager == null || !scrollManager.isVerticallyScrollable()) {
            return;
        }

        // Draw a simple scrollbar
        int barWidth = 10;
        int barX = width - barWidth;

        // Background track
        g.setColor(new Color(200, 200, 200, 150));
        g.fillRect(barX, 0, barWidth, height);

        // Calculate thumb position and size
        float thumbRatio = (float)scrollManager.getViewportHeight() / scrollManager.getContentHeight();
        int thumbHeight = Math.max(20, (int)(height * thumbRatio));
        int thumbY = (int)(scrollManager.getVerticalScrollPercentage() * (height - thumbHeight));

        // Draw thumb
        g.setColor(new Color(100, 100, 100, 200));
        g.fillRect(barX, thumbY, barWidth, thumbHeight);
    }

    // Focus Management ================================================================================================
    @Override
    public final void onFocus() {
        // Remove focus from siblings
        if (parent != null) {
            for (var sibling : parent.children) {
                if (sibling != this) {
                    sibling.onUnfocus();
                }
            }
        }

        isFocused = true;

        if (guiFocusHandler != null) {
            guiFocusHandler.onFocus();
        }
    }

    private void onUnfocus() {
        isFocused = false;

        for (var child : children) {
            child.onUnfocus();
        }
    }

    // Child Management ================================================================================================
    public void addChild(GuiElement child) {
        children.add(child);
        child.parent = this;
        performLayout();
    }

    public void removeChild(GuiElement child) {
        children.remove(child);
        child.parent = null;
        performLayout();
    }

    public final List<GuiElement> getChildren() {
        return children;
    }

    // In GuiElement class
    public void performLayout() {
        if (layoutManager != null) {
            // Calculate content size based on children
            int contentWidth = 0;
            int contentHeight = 0;

            // Let layout manager arrange children
            layoutManager.onLayout(this);

            // After layout, determine total content size
            for (GuiElement child : children) {
                contentWidth = Math.max(contentWidth, child.getX() + child.getWidth());
                contentHeight = Math.max(contentHeight, child.getY() + child.getHeight());
            }

            // Update scroll manager with content size
            if (scrollManager != null) {
                scrollManager.setContentSize(contentWidth, contentHeight);
                scrollManager.onContentLayoutChanged();
            }
        }
    }

    // Resize Management ===============================================================================================
    public final void setWidth(int width) {
        int oldWidth = this.width;
        this.width = Math.max(0, width);
        if (oldWidth == this.width) {
            return;
        }

        performLayout();
    }

    public final void setHeight(int height) {
        int oldHeight = this.height;
        this.height = Math.max(0, height);
        if (oldHeight == this.height) {
            return;
        }

        performLayout();
    }

    // Checks if the mouse coordinates are within this component's viewport
    private boolean contains(int mouseX, int mouseY) {
        // First check basic bounds
        if (mouseX < x || mouseX >= x + width || mouseY < y || mouseY >= y + height) {
            return false;
        }

        // If not using rounded corners, we're done with the simple check
        if (cornerRadius == 0) {
            return true;
        }

        // Create a rounded rectangle for the component's bounds
        Rectangle rect = new Rectangle(x, y, width, height);
        Area roundedRect = new Area(rect);
        roundedRect.add(new Area(new Rectangle(x + cornerRadius, y, width - 2 * cornerRadius, height)));
        roundedRect.add(new Area(new Rectangle(x, y + cornerRadius, width, height - 2 * cornerRadius)));
        roundedRect.add(new Area(new Rectangle(x + width - cornerRadius, y, cornerRadius, cornerRadius)));
        roundedRect.add(new Area(new Rectangle(x, y + height - cornerRadius, cornerRadius, cornerRadius)));
        roundedRect.add(new Area(new Rectangle(x + width - cornerRadius, y + height - cornerRadius, cornerRadius, cornerRadius)));

        return roundedRect.contains(mouseX, mouseY);
    }


    // Hover Management ================================================================================================
    @Override
    public final void onHover() {
        if (guiHoverHandler != null) {
            guiHoverHandler.onHover();
        }
    }

    // Key Event Handling ==============================================================================================
    private <T> void handleKeyEvent(T keyEvent, BiConsumer<GuiKeyHandler, T> eventHandler) {
        // If the element is not active, do not process the event
        if (!isEnabled || !isVisible || !isFocused) return;

        // Delegate the event to the key handler of this element
        if (guiKeyHandler != null) {
            eventHandler.accept(guiKeyHandler, keyEvent);
        }

        // Delegate the event to the key handler of the first child that is focused
        for (var child : children) {
            if (!child.isFocused) continue;
            eventHandler.accept(child.guiKeyHandler, keyEvent);
            break;
        }
    }

    @Override
    public final void onKeyPressed(int keyCode) {
        handleKeyEvent(keyCode, GuiKeyHandler::onKeyPressed);
    }

    @Override
    public final void onKeyReleased(int keyCode) {
        handleKeyEvent(keyCode, GuiKeyHandler::onKeyReleased);
    }

    @Override
    public final void onKeyTyped(char keyChar) {
        handleKeyEvent(keyChar, GuiKeyHandler::onKeyTyped);
    }

    // Mouse Event Handling ============================================================================================
    private <T> void handleMouseEvent(T mouseEvent, BiConsumer<GuiMouseHandler, T> eventHandler) {
        // NOTE: This method is probably incorrect, but it is just a placeholder for now

        // If the element is not active, do not process the event
        if (!isEnabled || !isVisible || !isFocused) return;

        // Delegate the event to the mouse handler of this element
        if (guiMouseHandler != null) {
            eventHandler.accept(guiMouseHandler, mouseEvent);
        }

        // Delegate the event to the mouse handler of the first child that is focused
        for (var child : children) {
            if (!child.isFocused) continue;
            eventHandler.accept(child.guiMouseHandler, mouseEvent);
            break;
        }
    }



    @Override
    public final void onMouseReleased(MouseEvent e) {
        handleMouseEvent(e, GuiMouseHandler::onMouseReleased);
    }

    @Override
    public final void onMousePressed(MouseEvent e) {

    }

    @Override
    public final void onMouseClicked(MouseEvent e) {
        if (!isEnabled || !isVisible || e.isConsumed()) return;

        // Convert to local coordinates (within this element's frame)
        Point local = windowToLocal(e.getX(), e.getY());

        if (local.x < 0 || local.x >= width || local.y < 0 || local.y >= height) {
            return; // Click is outside our bounds so we ignore it
        }

        // Check if it's on a scrollbar (if we have one)
        if (scrollManager != null && isPointOnScrollbar(local.x, local.y)) {
            handleScrollbarClick(local.x, local.y);
            e.consume();
            return;
        }

        // Check if point is within visible area (considering clipping)
        if (scrollManager != null) {
            Rectangle visibleBounds = new Rectangle(
                    scrollManager.getScrollXOffset(),
                    scrollManager.getScrollYOffset(),
                    width,
                    height
            );

            // Convert to content coordinates
            Point content = localToContent(local.x, local.y);

            // If point is outside visible area, ignore it
            if (!visibleBounds.contains(content.x, content.y)) {
                return;
            }
        }

        // Handle in children first
        boolean handled = false;
        for (int i = children.size() - 1; i >= 0; i--) {
            GuiElement child = children.get(i);
            if (!child.isVisible) continue;

            // Create a child event with coordinates translated to window space
            MouseEvent childEvent = new MouseEvent(
                    e.getComponent(),
                    e.getID(),
                    e.getWhen(),
                    e.getModifiers(),
                    e.getX(), // Keep original window coordinates
                    e.getY(), // Each child will convert as needed
                    e.getClickCount(),
                    e.isPopupTrigger(),
                    e.getButton()
            );

            child.onMouseClicked(childEvent);
            if (childEvent.isConsumed()) {
                e.consume();
                handled = true;
                break;
            }
        }

        // If no child handled it, handle it ourselves
        if (!handled && guiMouseHandler != null) {
            guiMouseHandler.onMouseClicked(e);
        }
        e.consume();

    }

    // Helper method to check if a point is on the scrollbar
    private boolean isPointOnScrollbar(int localX, int localY) {
        if (scrollManager == null || !scrollManager.isVerticallyScrollable()) {
            return false;
        }

        int barWidth = 10; // Should match the width used in renderScrollbars
        int barX = width - barWidth;

        return localX >= barX && localX < width && localY >= 0 && localY < height;
    }

    // Helper method to handle scrollbar clicks
    private void handleScrollbarClick(int localX, int localY) {
        if (scrollManager == null) return;

        // Calculate thumb position and size
        float thumbRatio = (float)scrollManager.getViewportHeight() / scrollManager.getContentHeight();
        int thumbHeight = Math.max(20, (int)(height * thumbRatio));
        int thumbY = (int)(scrollManager.getVerticalScrollPercentage() * (height - thumbHeight));

        if (localY < thumbY) {
            // Clicked above thumb - scroll up one page
            scrollManager.onScroll(0, -scrollManager.getViewportHeight());
        } else if (localY >= thumbY + thumbHeight) {
            // Clicked below thumb - scroll down one page
            scrollManager.onScroll(0, scrollManager.getViewportHeight());
        } else {
            // Clicked on thumb - could start a drag operation here
            // This would require additional state tracking
        }
    }


    @Override
    public final void onMouseDragged(MouseEvent e) {
        handleMouseEvent(e, GuiMouseHandler::onMouseDragged);
    }

    @Override
    public final void onMouseMoved(MouseEvent e) {
        handleMouseEvent(e, GuiMouseHandler::onMouseMoved);
    }

    @Override
    public final void onMouseWheelMoved(MouseWheelEvent e) {
        if (!isEnabled || !isVisible) return;

        // Check if mouse is over this component
        if (contains(e.getX(), e.getY())) {
            // Handle scrolling if this component has a scroll manager
            if (scrollManager != null) {
                // Default wheel unit is usually 1, multiply by pixels to scroll
                scrollManager.onScroll(0, e.getWheelRotation() * 10);
                e.consume();
            }

            // Propagate to children if not consumed
            if (!e.isConsumed()) {
                for (var child : children) {
                    if (child.isVisible) {
                        child.onMouseWheelMoved(e);
                    }
                }
            }
        }
    }

    @Override
    public final void onMouseEntered(MouseEvent e) {
        handleMouseEvent(e, GuiMouseHandler::onMouseEntered);
    }

    @Override
    public final void onMouseExited(MouseEvent e) {
        handleMouseEvent(e, GuiMouseHandler::onMouseExited);
    }

    public GuiScrollManager getScrollManager() {
        return scrollManager;
    }

    // This handles converting a point in window coordinates to local element coordinates
    // Local space refers to the element's own coordinate system relative to the element's position
    private Point windowToLocal(int windowX, int windowY) {
        // First, account for element position
        int localX = windowX - x;
        int localY = windowY - y;
        return new Point(localX, localY);
    }

    // This handles converting a point in local coordinates to content coordinates (with scrolling)
    // Content space refers to the full content area and may be larger than what's visible and can be scrolled
    private Point localToContent(int localX, int localY) {
        if (scrollManager == null) {
            return new Point(localX, localY);
        }

        // Add scroll offsets to get content coordinates
        int contentX = localX + scrollManager.getScrollXOffset();
        int contentY = localY + scrollManager.getScrollYOffset();
        return new Point(contentX, contentY);
    }

    private Point contentToLocal(int contentX, int contentY) {
        if (scrollManager == null) {
            return new Point(contentX, contentY);
        }

        // Subtract scroll offsets to get local coordinates
        int localX = contentX - scrollManager.getScrollXOffset();
        int localY = contentY - scrollManager.getScrollYOffset();
        return new Point(localX, localY);
    }

    private Point localToWindow(int localX, int localY) {
        // First, account for element position
        int windowX = localX + x;
        int windowY = localY + y;
        return new Point(windowX, windowY);
    }

    // This combines both transformations - window to content
    private Point windowToContent(int windowX, int windowY) {
        Point local = windowToLocal(windowX, windowY);
        return localToContent(local.x, local.y);
    }

    private Point contentToWindow(int contentX, int contentY) {
        Point local = contentToLocal(contentX, contentY);
        return localToWindow(local.x, local.y);
    }
}
