package client.core.gui.control;

import client.core.graphics.Renderer;
import client.core.gui.GuiComponent;
import client.core.gui.input.GuiEventState;
import client.core.gui.manager.GuiMouseCaptureManager;
import client.core.input.MouseEvent;
import client.core.util.Util;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.function.Consumer;

public class GuiSlider extends GuiComponent {
    // Value range properties
    @Getter
    @Setter
    private double minValue = 0;
    @Getter
    @Setter
    private double maxValue = 100;
    @Getter
    @Setter
    private double value = 50;
    @Getter
    @Setter
    private boolean vertical = false;
    // Styling properties
    @Getter
    @Setter
    private Color trackColor = new Color(200, 200, 200);
    @Getter
    @Setter
    private Color thumbColor = new Color(100, 100, 150);
    @Getter
    @Setter
    private Color thumbHoverColor = new Color(120, 120, 180);
    @Getter
    @Setter
    private Color thumbActiveColor = new Color(80, 80, 130);
    // Thumb dimensions
    @Getter
    @Setter
    private int thumbWidth = 16;
    @Getter
    @Setter
    private int thumbHeight = 24;
    // State tracking for drag operations
    private boolean isDragging = false;
    private Point dragLastLocation = null;
    // Value change callback
    @Setter
    private Consumer<Double> onValueChanged = null;

    public GuiSlider(int width, int height) {
        super(0, 0, width, height);
    }

    @Override
    protected void onRender(Renderer g) {
        g.setColor(Color.RED);
        g.fillRect(0, 0, width, height);

        if (vertical) {
            renderVerticalSlider(g);
        } else {
            renderHorizontalSlider(g);
        }
    }

    private void renderHorizontalSlider(Renderer g) {
        // Draw track
        int trackHeight = 6;
        int trackY = (height - trackHeight) / 2;
        g.setColor(trackColor);
        g.fillRect(0, trackY, width, trackHeight);

        // Draw thumb
        int thumbX = valueToPosition();
        int thumbY = (height - thumbHeight) / 2;

        // Choose thumb color based on state
        if (isDragging) {
            g.setColor(thumbActiveColor);
        } else if (isHovered) {
            g.setColor(thumbHoverColor);
        } else {
            g.setColor(thumbColor);
        }

        g.fillRect(thumbX - thumbWidth / 2, thumbY, thumbWidth, thumbHeight);

        // Draw thumb border
        g.setColor(Color.DARK_GRAY);
        g.drawRect(thumbX - thumbWidth / 2, thumbY, thumbWidth, thumbHeight);
    }

    private void renderVerticalSlider(Renderer g) {
        // Draw track
        int trackWidth = 6;
        int trackX = (width - trackWidth) / 2;
        g.setColor(trackColor);
        g.fillRect(trackX, 0, trackWidth, height);

        // Draw thumb
        int thumbX = (width - thumbWidth) / 2;
        int thumbY = valueToPosition();

        // Choose thumb color based on state
        if (isDragging) {
            g.setColor(thumbActiveColor);
        } else if (isHovered) {
            g.setColor(thumbHoverColor);
        } else {
            g.setColor(thumbColor);
        }

        g.fillRect(thumbX, thumbY - thumbHeight / 2, thumbWidth, thumbHeight);

        // Draw thumb border
        g.setColor(Color.DARK_GRAY);
        g.drawRect(thumbX, thumbY - thumbHeight / 2, thumbWidth, thumbHeight);
    }

    private int valueToPosition() {
        // Converts the current value to a position on the slider
        double valueRange = maxValue - minValue;
        double valuePercent = (value - minValue) / valueRange;

        return vertical
                ? (int) (valuePercent * height)
                : (int) (valuePercent * width);
    }

    private double positionToValue(int position) {
        // Converts the given position to a slider value
        double percent = vertical
                ? 1.0 - (double) position / height
                : (double) position / width;

        // Clamp percent to 0.0-1.0 range
        percent = Math.max(0.0, Math.min(1.0, percent));

        // Convert to actual value
        return minValue + percent * (maxValue - minValue);
    }

    private boolean isPointOnThumb(Point point) {
        if (vertical) {
            int thumbY = valueToPosition();
            int thumbX = (width - thumbWidth) / 2;

            return point.x >= thumbX && point.x <= thumbX + thumbWidth &&
                    point.y >= thumbY - thumbHeight / 2 && point.y <= thumbY + thumbHeight / 2;
        } else {
            int thumbX = valueToPosition();
            int thumbY = (height - thumbHeight) / 2;

            return point.x >= thumbX - thumbWidth / 2 && point.x <= thumbX + thumbWidth / 2 &&
                    point.y >= thumbY && point.y <= thumbY + thumbHeight;
        }
    }

    @Override
    public GuiEventState processMouseEvent(MouseEvent e) {
        // Early return if not visible nor enabled
        if (!isVisible || !isEnabled) return GuiEventState.NOT_CONSUMED;

        Point point = e.getPoint();

        // If we're not in bounds and not captured by a drag operation, ignore the event
        if (!containsPoint(point) && !GuiMouseCaptureManager.getInstance().isCapturedComponent(this)) {
            return GuiEventState.NOT_CONSUMED;
        }

        switch (e) {
            // Continue drag operation
            case MouseEvent.Dragged _ when isDragging && GuiMouseCaptureManager.getInstance().isCapturedComponent(this) -> {
                updateDrag(point);
                return GuiEventState.CONSUMED;
            }

            // End drag operation
            case MouseEvent.Released _ when isDragging && GuiMouseCaptureManager.getInstance().isCapturedComponent(this) -> {
                stopDrag();
                return GuiEventState.CONSUMED;
            }

            // Start drag when clicking on the thumb
            case MouseEvent.Pressed _ when isPointOnThumb(point) -> {
                startDrag(point);
                return GuiEventState.CONSUMED;
            }

            // Jump thumb when clicking elsewhere
            case MouseEvent.Pressed _ when !isPointOnThumb(point) -> {
                jumpThumb(point);
                return GuiEventState.CONSUMED;
            }

            // Handle mouse movement
            case MouseEvent.Moved _ when containsPoint(point) -> {
                return super.processMouseEvent(e);
            }

            default -> {
                //  Empty
            }
        }

        return GuiEventState.NOT_CONSUMED;
    }

    private void startDrag(Point point) {
        isDragging = true;
        dragLastLocation = null;
        value = positionToValue(vertical ? point.y : point.x);
        GuiMouseCaptureManager.getInstance().setMouseCapture(this);
    }

    private void stopDrag() {
        // End dragging operation
        isDragging = false;
        dragLastLocation = null;
        GuiMouseCaptureManager.getInstance().releaseMouseCapture();
    }

    private void updateDrag(Point point) {
        double delta = dragLastLocation != null ?
                (vertical ? point.y - dragLastLocation.y : point.x - dragLastLocation.x) : 0;

        double currentThumbPosition = valueToPosition();
        currentThumbPosition += delta;
        currentThumbPosition = Util.clamp(currentThumbPosition, 0, vertical ? height : width);
        value = positionToValue((int) currentThumbPosition);

        // Update last position
        dragLastLocation = point;

        // Trigger callback
        if (onValueChanged != null) {
            onValueChanged.accept(value);
        }
    }

    private void jumpThumb(Point point) {
        positionToValue(vertical ? point.y : point.x);
    }

    @Override
    protected String getComponentName() {
        return "slider";
    }
}