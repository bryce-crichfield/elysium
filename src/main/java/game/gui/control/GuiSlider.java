package game.gui.control;

import game.input.MouseEvent;
import game.gui.GuiComponent;
import game.gui.input.GuiMouseManager;
import game.platform.Renderer;
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
    private Point dragStart = null;
    private double dragStartValue = 0;

    // Value change callback
    private Consumer<Double> onValueChanged = null;

    public GuiSlider(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public void setOnValueChanged(Consumer<Double> callback) {
        this.onValueChanged = callback;
    }

    @Override
    protected void onRender(Renderer g) {
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
        double valueRange = maxValue - minValue;
        double valuePercent = (value - minValue) / valueRange;

        if (vertical) {
            // For vertical slider, 0 is at the bottom, 100 at the top
            return height - (int)(valuePercent * height);
        } else {
            // For horizontal slider, 0 is at the left, 100 at the right
            return (int)(valuePercent * width);
        }
    }

    private double positionToValue(int position) {
        double percent;

        if (vertical) {
            // Invert calculation for vertical slider
            percent = 1.0 - (double)position / height;
        } else {
            percent = (double)position / width;
        }

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
    public boolean processMouseEvent(MouseEvent e) {
        if (!visible || !enabled) return false;

        Point localPoint = transformToLocalSpace(e.getPoint());

        // Check if we're in bounds for hover state
        boolean containsPoint = containsPoint(localPoint);

        // Update hover state
        if (e instanceof MouseEvent.Pressed) {
            isHovered = containsPoint;
        }

        // If we're not in bounds and not captured by a drag operation, ignore the event
        if (!containsPoint && !GuiMouseManager.isCapturedComponent(this)) return false;

        // Handle ongoing drag operation
        if (isDragging && GuiMouseManager.isCapturedComponent(this)) {
            if (e instanceof MouseEvent.Dragged) {
                // Calculate new value based on drag position
                double newValue;

                if (vertical) {
                    // Determine value based on vertical position
                    newValue = positionToValue(localPoint.y);
                } else {
                    // Determine value based on horizontal position
                    newValue = positionToValue(localPoint.x);
                }

                // Only trigger value changed event if the value actually changed
                if (newValue != value) {
                    setValue(newValue);

                    // Trigger callback if set
                    if (onValueChanged != null) {
                        onValueChanged.accept(value);
                    }
                }

                return true;
            }
            else if (e instanceof MouseEvent.Released) {
                // End dragging operation
                isDragging = false;
                dragStart = null;
                GuiMouseManager.releaseMouseCapture();
                return true;
            }
        }

        // Start new drag operation
        if (e instanceof MouseEvent.Pressed) {
            // Check if the click is directly on the thumb
            boolean onThumb = isPointOnThumb(localPoint);

            // If we clicked on the thumb, start dragging
            if (onThumb) {
                isDragging = true;
                dragStart = new Point(localPoint);
                dragStartValue = value;
                GuiMouseManager.setMouseCapture(this);
                return true;
            }
            // If we clicked on the track, jump to that position
            else if (containsPoint) {
                double newValue;

                if (vertical) {
                    newValue = positionToValue(localPoint.y);
                } else {
                    newValue = positionToValue(localPoint.x);
                }

                if (newValue != value) {
                    setValue(newValue);

                    if (onValueChanged != null) {
                        onValueChanged.accept(value);
                    }
                }

                return true;
            }
        }

        // Pass to mouseMoved handlers if it's a move event
        if (e instanceof MouseEvent.Moved && containsPoint) {
            return super.processMouseEvent(e);
        }

        return false;
    }
}