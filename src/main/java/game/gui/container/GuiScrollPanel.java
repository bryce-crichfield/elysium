package game.gui.container;

import game.gui.GuiComponent;
import game.gui.input.GuiEventState;
import game.input.MouseEvent;
import game.gui.input.GuiHoverManager;
import game.gui.input.GuiMouseManager;
import game.graphics.Renderer;
import game.graphics.Transform;
import lombok.Getter;

import java.awt.*;

public class GuiScrollPanel extends GuiContainer {
    protected GuiScrollState scrollState = new GuiScrollState();
    protected boolean showHorizontalScrollbar = true;
    protected boolean showVerticalScrollbar = true;
    protected boolean isHorizontalBarVisible = false;
    protected boolean isVerticalBarVisible = false;

    // Scrollbar dragging state
    private boolean isDraggingVertical = false;
    private boolean isDraggingHorizontal = false;
    private Point dragStart = null;
    private float dragStartScrollX = 0;
    private float dragStartScrollY = 0;

    public GuiScrollPanel(int x, int y, int width, int height) {
        super(x, y, width, height);
        scrollState.setViewportSize(width, height);
    }

    @Override
    public void addChild(GuiComponent child) {
        super.addChild(child);
        updateContentSize();
    }

    private void updateContentSize() {
        int contentWidth = 0;
        int contentHeight = 0;

        // Calculate content size based on children positions and sizes
        for (GuiComponent child : children) {
            contentWidth = Math.max(contentWidth, child.getX() + child.getWidth());
            contentHeight = Math.max(contentHeight, child.getY() + child.getHeight());
        }

        scrollState.setContentSize(contentWidth, contentHeight);

        // Update scrollbar visibility
        isHorizontalBarVisible = showHorizontalScrollbar && scrollState.isHorizontallyScrollable();
        isVerticalBarVisible = showVerticalScrollbar && scrollState.isVerticallyScrollable();
    }

    @Override
    protected void onRender(Renderer renderer) {
        // Save the original clip
//        Shape originalClip = renderer.getClip();

        // Intersect with our viewport to create a new clip
//        Rectangle viewportRect = new Rectangle(0, 0, width, height);
//        renderer.clip(viewportRect);
        renderer.pushClip(0, 0, width, height);

        // Render container background
        if (background != null) {
            background.render(renderer, width, height, 0);
        }

        if (border != null) {
            border.render(renderer, width, height, 0);
        }

        // Push scroll translation
//        Transform scrollTransform = renderer.getTransform().copy();
//        scrollTransform.translate((int) -scrollState.getScrollXOffset(), (int) -scrollState.getScrollYOffset());
        var scrollTransform = Transform.createTranslate((int) -scrollState.getScrollXOffset(), (int) -scrollState.getScrollYOffset());
        renderer.pushTransform(scrollTransform);

        // Render children
        for (GuiComponent child : children) {
            child.render(renderer);
        }

        // Pop back to pre-scroll apply
        renderer.popTransform();

        // Restore clip
        renderer.popClip();
//        renderer.setClip(originalClip);

        // Render scrollbars
        renderScrollbars(renderer);
    }


    private void renderScrollbars(Renderer renderer) {
        if (isVerticalBarVisible) {
            renderVerticalScrollbar(renderer);
        }

        if (isHorizontalBarVisible) {
            renderHorizontalScrollbar(renderer);
        }
    }

    private void renderHorizontalScrollbar(Renderer renderer) {
        // Horizontal scrollbar track
        Rectangle trackBounds = scrollState.getHorizontalScrollbarBounds(width, height);
        renderer.setColor(new Color(200, 200, 200, 150));
        renderer.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);

        // Horizontal thumb
        Rectangle thumbBounds = scrollState.getHorizontalThumbBounds(width, height);
        if (thumbBounds != null) {
            renderer.setColor(isDraggingHorizontal ?
                    new Color(80, 80, 80, 200) :
                    new Color(120, 120, 120, 180));
            renderer.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);

            // Thumb grip lines
            renderer.setColor(new Color(180, 180, 180, 150));
            int centerX = thumbBounds.x + thumbBounds.width / 2;
            int lineHeight = thumbBounds.height - 4;
            renderer.drawLine(centerX - 3, thumbBounds.y + 2, centerX - 3, thumbBounds.y + 2 + lineHeight);
            renderer.drawLine(centerX, thumbBounds.y + 2, centerX, thumbBounds.y + 2 + lineHeight);
            renderer.drawLine(centerX + 3, thumbBounds.y + 2, centerX + 3, thumbBounds.y + 2 + lineHeight);
        }
    }

    private void renderVerticalScrollbar(Renderer renderer) {
        // Vertical scrollbar track
        Rectangle trackBounds = scrollState.getVerticalScrollbarBounds(width, height);
        renderer.setColor(new Color(200, 200, 200, 150));
        renderer.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);

        // Vertical thumb
        Rectangle thumbBounds = scrollState.getVerticalThumbBounds(width, height);
        if (thumbBounds != null) {
            renderer.setColor(isDraggingVertical ?
                    new Color(80, 80, 80, 200) :
                    new Color(120, 120, 120, 180));
            renderer.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);

            // Thumb grip lines
            renderer.setColor(new Color(180, 180, 180, 150));
            int centerY = thumbBounds.y + thumbBounds.height / 2;
            int lineWidth = thumbBounds.width - 4;
            renderer.drawLine(thumbBounds.x + 2, centerY - 3, thumbBounds.x + 2 + lineWidth, centerY - 3);
            renderer.drawLine(thumbBounds.x + 2, centerY, thumbBounds.x + 2 + lineWidth, centerY);
            renderer.drawLine(thumbBounds.x + 2, centerY + 3, thumbBounds.x + 2 + lineWidth, centerY + 3);
        }
    }

    @Override
    public GuiEventState processMouseEvent(MouseEvent e) {
        if (!isVisible || !isEnabled) return GuiEventState.NOT_CONSUMED;

        // Convert to local space first
        Point point = e.getPoint();
        // Transform the point to local space
        Point localPoint = new Point(point.x - x, point.y - y);
        boolean isInBounds = containsPoint(localPoint) || isPointOnScrollbar(localPoint);

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

        // Check that we are not hovered and not captured by some dragging event
        if (!isHovered && !GuiMouseManager.isCapturedComponent(this)) return GuiEventState.NOT_CONSUMED;

        // Check to see if there is a scroll bar interaction (click, release or drag).  If there isn't, but we are hovered
        // return true to ensure we consume the event.
        if (isPointOnScrollbar(localPoint) || GuiMouseManager.isCapturedComponent(this)) {
//            var localEvent = Mouse.translateEvent(e, localPoint.x, localPoint.y);
            var localEvent = e.withPoint(localPoint);
            boolean handledScrollbar = handleScrollbarInteraction(localEvent, localPoint);
            if (handledScrollbar || isHovered) return GuiEventState.CONSUMED;
        }

        // Handle mouse wheel for scrolling
        if (e instanceof MouseEvent.WheelMoved wheelMoved) {
            float scrollAmount = wheelMoved.getWheelRotation() * 20;
            scrollState.scroll(0, scrollAmount);
            return GuiEventState.CONSUMED;
        }

        // Adjust coordinates for content area
        Point contentPoint = new Point(
                localPoint.x + (int) scrollState.getScrollXOffset(),
                localPoint.y + (int) scrollState.getScrollYOffset()
        );

        // Create adjusted event
//        var contentEvent = Mouse.translateEvent(e, contentPoint.x, contentPoint.y);
        var contentEvent = e.withPoint(contentPoint);

        // Let children handle it
        for (int i = children.size() - 1; i >= 0; i--) {
            GuiComponent child = children.get(i);
            if (child.processMouseEvent(contentEvent) == GuiEventState.CONSUMED) {
                return GuiEventState.CONSUMED;
            }
        }

        // Handle by us or our handlers
        if (onMouseEvent(contentEvent) == GuiEventState.CONSUMED) return GuiEventState.CONSUMED;

        for (var handler : mouseHandlers) {
            if (contentEvent.isConsumed()) break;
            handler.dispatchMouseEvent(contentEvent);
        }

        return GuiEventState.CONSUMED;
    }

    private boolean handleScrollbarInteraction(MouseEvent e, Point localPoint) {
        // Handle ongoing drag events
        if (isDraggingVertical || isDraggingHorizontal) {
            if (e instanceof MouseEvent.Dragged) {
                // Always recalculate drag delta from original start point
                Point dragDelta = new Point(
                        localPoint.x - dragStart.x,
                        localPoint.y - dragStart.y
                );

                if (isDraggingVertical) {
                    // Calculate scrolling based on drag delta
                    float trackHeight = scrollState.getVerticalScrollbarBounds(width, height).height;
                    float contentVisibleRatio = (float) height / scrollState.getContentHeight();
                    float scrollFactor = 1 / contentVisibleRatio;

                    // Directly set scroll position
                    scrollState.scrollTo(
                            dragStartScrollX,
                            dragStartScrollY + (dragDelta.y * scrollFactor)
                    );
                } else if (isDraggingHorizontal) {
                    float trackWidth = scrollState.getHorizontalScrollbarBounds(width, height).width;
                    float contentVisibleRatio = (float) width / scrollState.getContentWidth();
                    float scrollFactor = 1 / contentVisibleRatio;

                    scrollState.scrollTo(
                            dragStartScrollX + (dragDelta.x * scrollFactor),
                            dragStartScrollY
                    );
                }
                return true;
            }
            // Release from the current drag
            else if (e instanceof MouseEvent.Released) {
                isDraggingVertical = false;
                isDraggingHorizontal = false;
                dragStart = null;
                GuiMouseManager.releaseMouseCapture();
                return true;
            }
        }

        // Handle the start of new drag events
        if (e instanceof MouseEvent.Pressed) {
            // Check vertical scrollbar
            if (isVerticalBarVisible) {
                Rectangle thumbBounds = scrollState.getVerticalThumbBounds(width, height);
                if (thumbBounds != null && thumbBounds.contains(localPoint)) {
                    isDraggingVertical = true;
                    isDraggingHorizontal = false; // Make sure horizontal is off
                    dragStart = new Point(localPoint); // Create new point to avoid reference issues
                    dragStartScrollX = scrollState.getScrollXOffset();
                    dragStartScrollY = scrollState.getScrollYOffset();
                    GuiMouseManager.setMouseCapture(this); // Capture mouse events
                    return true;
                }

                Rectangle trackBounds = scrollState.getVerticalScrollbarBounds(width, height);
                if (trackBounds.contains(localPoint)) {
                    // Click on track - page up/down
                    Rectangle thumb = scrollState.getVerticalThumbBounds(width, height);
                    if (localPoint.y < thumb.y) {
                        // Page up
                        scrollState.scroll(0, -height);
                    } else {
                        // Page down
                        scrollState.scroll(0, height);
                    }
                    return true;
                }
            }

            // Check horizontal scrollbar
            if (isHorizontalBarVisible) {
                Rectangle thumbBounds = scrollState.getHorizontalThumbBounds(width, height);
                if (thumbBounds != null && thumbBounds.contains(localPoint)) {
                    isDraggingHorizontal = true;
                    isDraggingVertical = false; // Make sure vertical is off
                    dragStart = new Point(localPoint);
                    dragStartScrollX = scrollState.getScrollXOffset();
                    dragStartScrollY = scrollState.getScrollYOffset();
                    GuiMouseManager.setMouseCapture(this); // Capture mouse events
                    return true;
                }

                Rectangle trackBounds = scrollState.getHorizontalScrollbarBounds(width, height);
                if (trackBounds.contains(localPoint)) {
                    // Click on track - page left/right
                    Rectangle thumb = scrollState.getHorizontalThumbBounds(width, height);
                    if (localPoint.x < thumb.x) {
                        // Page left
                        scrollState.scroll(-width, 0);
                    } else {
                        // Page right
                        scrollState.scroll(width, 0);
                    }
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isPointOnScrollbar(Point localPoint) {
        var verticalBounds = scrollState.getVerticalScrollbarBounds(width, height);
        var horizontalBounds = scrollState.getHorizontalScrollbarBounds(width, height);
        if (isVerticalBarVisible && verticalBounds.contains(localPoint)) return true;
        return isHorizontalBarVisible && horizontalBounds.contains(localPoint);
    }

    public void scrollTo(float x, float y) {
        scrollState.scrollTo(x, y);
    }

    public void scrollBy(float deltaX, float deltaY) {
        scrollState.scroll(deltaX, deltaY);
    }

    public void scrollToTop() {
        scrollState.scrollTo(scrollState.getScrollXOffset(), 0);
    }

    public void scrollToBottom() {
        scrollState.scrollTo(scrollState.getScrollXOffset(), scrollState.getMaxScrollY());
    }

    public static class GuiScrollState {
        // Scrollbar constants
        private static final int SCROLLBAR_SIZE = 10;
        private static final int MIN_THUMB_SIZE = 20;
        private float scrollX = 0;
        private float scrollY = 0;
        @Getter
        private int contentWidth = 0;
        @Getter
        private int contentHeight = 0;
        private int viewportWidth = 0;
        private int viewportHeight = 0;

        public GuiScrollState() {
        }

        public void setViewportSize(int width, int height) {
            this.viewportWidth = width;
            this.viewportHeight = height;
            validateScrollPosition();
        }

        public void setContentSize(int width, int height) {
            this.contentWidth = width;
            this.contentHeight = height;
            validateScrollPosition();
        }

        public void scroll(float deltaX, float deltaY) {
            scrollX += deltaX;
            scrollY += deltaY;
            validateScrollPosition();
        }

        public void scrollTo(float x, float y) {
            scrollX = x;
            scrollY = y;
            validateScrollPosition();
        }

        private void validateScrollPosition() {
            // Constrain scrolling to valid ranges
            scrollX = Math.max(0, Math.min(scrollX, getMaxScrollX()));
            scrollY = Math.max(0, Math.min(scrollY, getMaxScrollY()));
        }

        public float getMaxScrollX() {
            return Math.max(0, contentWidth - viewportWidth);
        }

        public float getMaxScrollY() {
            return Math.max(0, contentHeight - viewportHeight);
        }

        public boolean isHorizontallyScrollable() {
            return contentWidth > viewportWidth;
        }

        public boolean isVerticallyScrollable() {
            return contentHeight > viewportHeight;
        }

        public float getScrollXOffset() {
            return scrollX;
        }

        public float getScrollYOffset() {
            return scrollY;
        }

        // Scrollbar calculations
        public Rectangle getVerticalScrollbarBounds(int panelWidth, int panelHeight) {
            return new Rectangle(
                    panelWidth - SCROLLBAR_SIZE,
                    0,
                    SCROLLBAR_SIZE,
                    panelHeight - (isHorizontallyScrollable() ? SCROLLBAR_SIZE : 0)
            );
        }

        public Rectangle getHorizontalScrollbarBounds(int panelWidth, int panelHeight) {
            return new Rectangle(
                    0,
                    panelHeight - SCROLLBAR_SIZE,
                    panelWidth - (isVerticallyScrollable() ? SCROLLBAR_SIZE : 0),
                    SCROLLBAR_SIZE
            );
        }

        public Rectangle getVerticalThumbBounds(int panelWidth, int panelHeight) {
            if (!isVerticallyScrollable()) return null;

            Rectangle track = getVerticalScrollbarBounds(panelWidth, panelHeight);

            // Calculate thumb height as a proportion of visible content
            float ratio = (float) viewportHeight / contentHeight;
            int thumbHeight = Math.max(MIN_THUMB_SIZE, (int) (track.height * ratio));

            // Calculate thumb position
            float scrollRatio = scrollY / getMaxScrollY();
            int thumbY = track.y + (int) ((track.height - thumbHeight) * scrollRatio);

            return new Rectangle(track.x, thumbY, track.width, thumbHeight);
        }

        public Rectangle getHorizontalThumbBounds(int panelWidth, int panelHeight) {
            if (!isHorizontallyScrollable()) return null;

            Rectangle track = getHorizontalScrollbarBounds(panelWidth, panelHeight);

            // Calculate thumb width as a proportion of visible content
            float ratio = (float) viewportWidth / contentWidth;
            int thumbWidth = Math.max(MIN_THUMB_SIZE, (int) (track.width * ratio));

            // Calculate thumb position
            float scrollRatio = scrollX / getMaxScrollX();
            int thumbX = track.x + (int) ((track.width - thumbWidth) * scrollRatio);
            return new Rectangle(thumbX, track.y, thumbWidth, track.height);
        }
    }
}
