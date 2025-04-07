package game.gui.scroll;

import java.awt.*;
import java.time.Duration;

/**
 * A vertical scroll manager that handles scrolling for GUI elements.
 */
public class GuiScrollManager {
    private int scrollX = 0;
    private int scrollY = 0;

    private int contentWidth = 0;
    private int contentHeight = 0;

    private int viewportWidth = 0;
    private int viewportHeight = 0;

    private float scrollSpeed = 1.0f;

    // Target scroll position for smooth scrolling
    private int targetScrollY = 0;
    private float scrollAnimationSpeed = 8.0f; // Higher is faster
    private boolean smoothScrollingEnabled = false;

    public void setScrollOffset(int xOffset, int yOffset) {
        this.scrollX = Math.max(0, Math.min(xOffset, getMaxScrollX()));

        if (smoothScrollingEnabled) {
            this.targetScrollY = Math.max(0, Math.min(yOffset, getMaxScrollY()));
        } else {
            this.scrollY = Math.max(0, Math.min(yOffset, getMaxScrollY()));
        }
    }

    public int getScrollXOffset() {
        return scrollX;
    }

    public int getScrollYOffset() {
        return scrollY;
    }

    public void setContentSize(int width, int height) {
        this.contentWidth = width;
        this.contentHeight = height;

        // Clamp scroll offsets to valid range
        scrollX = Math.min(scrollX, getMaxScrollX());
        scrollY = Math.min(scrollY, getMaxScrollY());
        targetScrollY = Math.min(targetScrollY, getMaxScrollY());
    }

    public int getContentWidth() {
        return contentWidth;
    }

    public int getContentHeight() {
        return contentHeight;
    }

    public void setViewportSize(int width, int height) {
        this.viewportWidth = width;
        this.viewportHeight = height;

        // Clamp scroll offsets to valid range
        scrollX = Math.min(scrollX, getMaxScrollX());
        scrollY = Math.min(scrollY, getMaxScrollY());
        targetScrollY = Math.min(targetScrollY, getMaxScrollY());
    }

    public int getViewportWidth() {
        return viewportWidth;
    }

    public int getViewportHeight() {
        return viewportHeight;
    }

    public void setScrollSpeed(float speed) {
        this.scrollSpeed = Math.max(0.1f, speed);
    }

    public void onScroll(int deltaX, int deltaY) {
        int scaledDeltaY = (int) (deltaY * scrollSpeed);
        onScrollTo(scrollX + deltaX, smoothScrollingEnabled ? targetScrollY + scaledDeltaY : scrollY + scaledDeltaY);
    }

    public void onScrollTo(int x, int y) {
        scrollX = Math.max(0, Math.min(x, getMaxScrollX()));

        if (smoothScrollingEnabled) {
            targetScrollY = Math.max(0, Math.min(y, getMaxScrollY()));
        } else {
            scrollY = Math.max(0, Math.min(y, getMaxScrollY()));
        }
    }

    public boolean isScrollable() {
        return isVerticallyScrollable() || isHorizontallyScrollable();
    }

    public boolean isVerticallyScrollable() {
        return contentHeight > viewportHeight;
    }

    public boolean isHorizontallyScrollable() {
        return contentWidth > viewportWidth;
    }

    public boolean isAtTop() {
        return scrollY <= 0;
    }

    public boolean isAtBottom() {
        return scrollY >= getMaxScrollY();
    }

    public boolean isAtLeft() {
        return scrollX <= 0;
    }

    public boolean isAtRight() {
        return scrollX >= getMaxScrollX();
    }

    public void onUpdate(Duration delta) {
        if (smoothScrollingEnabled && scrollY != targetScrollY) {
            // Calculate frame-independent scrolling
            float deltaSeconds = delta.toNanos() / 1_000_000_000.0f;
            float step = scrollAnimationSpeed * deltaSeconds * Math.abs(targetScrollY - scrollY);

            if (Math.abs(targetScrollY - scrollY) < 1.0f) {
                scrollY = targetScrollY;
            } else if (targetScrollY > scrollY) {
                scrollY += Math.max(1, Math.min((int)step, targetScrollY - scrollY));
            } else {
                scrollY -= Math.max(1, Math.min((int)step, scrollY - targetScrollY));
            }
        }
    }

    public void onContentLayoutChanged() {
        // Ensure scroll position is still valid after content changes
        scrollX = Math.min(scrollX, getMaxScrollX());
        scrollY = Math.min(scrollY, getMaxScrollY());
        targetScrollY = Math.min(targetScrollY, getMaxScrollY());
    }

    public void onViewportLayoutChanged() {
        // Ensure scroll position is still valid after viewport changes
        scrollX = Math.min(scrollX, getMaxScrollX());
        scrollY = Math.min(scrollY, getMaxScrollY());
        targetScrollY = Math.min(targetScrollY, getMaxScrollY());
    }

    public Rectangle getVisibleContentBounds() {
        return new Rectangle(scrollX, scrollY, viewportWidth, viewportHeight);
    }

    public boolean shouldClipContent() {
        return true;
    }

    public Point translatePointToScrolledSpace(int x, int y) {
        return new Point(x + scrollX, y + scrollY);
    }

    public Point translatePointFromScrolledSpace(int x, int y) {
        return new Point(x - scrollX, y - scrollY);
    }

    /**
     * Returns the maximum valid X scroll offset
     */
    private int getMaxScrollX() {
        return Math.max(0, contentWidth - viewportWidth);
    }

    /**
     * Returns the maximum valid Y scroll offset
     */
    private int getMaxScrollY() {
        return Math.max(0, contentHeight - viewportHeight);
    }

    /**
     * Enables or disables smooth scrolling animation
     */
    public void setSmoothScrolling(boolean enabled) {
        this.smoothScrollingEnabled = enabled;
        if (!enabled) {
            scrollY = targetScrollY;
        }
    }

    /**
     * Sets the animation speed for smooth scrolling
     * Higher values make scrolling faster
     */
    public void setScrollAnimationSpeed(float speed) {
        this.scrollAnimationSpeed = Math.max(0.1f, speed);
    }

    /**
     * Gets the current scroll progress as a percentage (0.0 - 1.0)
     */
    public float getVerticalScrollPercentage() {
        if (contentHeight <= viewportHeight) return 0;
        return (float)scrollY / getMaxScrollY();
    }

    /**
     * Sets the scroll position based on a percentage (0.0 - 1.0)
     */
    public void scrollToPercentage(float percentage) {
        percentage = Math.max(0, Math.min(1, percentage));
        onScrollTo(scrollX, (int)(getMaxScrollY() * percentage));
    }
}
