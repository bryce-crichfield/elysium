package game.gui.control;

import game.gui.input.GuiEventState;
import game.input.MouseEvent;
import game.gui.GuiComponent;
import game.gui.input.GuiMouseManager;
import game.graphics.Renderer;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GuiDropdown<T> extends GuiComponent {
    // Main dropdown properties
    private final List<T> items = new ArrayList<>();
    @Getter
    @Setter
    private T selectedItem = null;
    private boolean isExpanded = false;

    // Styling properties
    @Getter
    @Setter
    private Color backgroundColor = new Color(240, 240, 240);
    @Getter
    @Setter
    private Color hoverColor = new Color(220, 220, 220);
    @Getter
    @Setter
    private Color textColor = Color.BLACK;
    @Getter
    @Setter
    private Color borderColor = new Color(200, 200, 200);

    // Item height for the dropdown list
    private final int itemHeight = 24;

    // The max height of the dropdown list when expanded
    private final int maxDropdownHeight = 150;

    // Selection callback
    private Consumer<T> onSelectionChanged = null;

    // Scrolling state for the dropdown list
    private final int scrollOffset = 0;

    // Z-order management - dropdowns should be rendered above other components
    private static GuiDropdown<?> activeDropdown = null;

    public GuiDropdown(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public void addItem(T item) {
        items.add(item);
    }

    public void setItems(List<T> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    public void setOnSelectionChanged(Consumer<T> callback) {
        this.onSelectionChanged = callback;
    }

    @Override
    protected void onRender(Renderer renderer) {
        // Draw the main button
        renderer.setColor(isHovered ? hoverColor : backgroundColor);
        renderer.fillRect(0, 0, width, height);

        // Draw border
        renderer.setColor(borderColor);
        renderer.drawRect(0, 0, width - 1, height - 1);

        // Draw selected text
        renderer.setColor(textColor);
        if (selectedItem != null) {
            String text = selectedItem.toString();
            drawTextCentered(renderer, text, 5, 0, width - 20, height);
        }

        // Draw dropdown arrow
        drawDropdownArrow(renderer, width - 15, height / 2, 8, isExpanded);

        // Draw dropdown menu if expanded
        if (isExpanded) {
            // We'll need to save the apply since we're breaking out of our bounds

            // Calculate the dropdown list height
            int dropdownListHeight = Math.min(maxDropdownHeight, items.size() * itemHeight);

            // Draw dropdown background
            renderer.setColor(backgroundColor);
            renderer.fillRect(0, height, width, dropdownListHeight);

            // Draw dropdown border
            renderer.setColor(borderColor);
            renderer.drawRect(0, height, width - 1, dropdownListHeight - 1);

            // Set clipping region for dropdown items
            renderer.pushClip(0, height, width, dropdownListHeight);
//            renderer.clipRect(0, height, width, dropdownListHeight);

            // Draw dropdown items
            int yPos = height - scrollOffset;
            for (int i = 0; i < items.size(); i++) {
                T item = items.get(i);
                if (yPos >= height && yPos < height + dropdownListHeight) {
                    // Check if this item is selected
                    boolean isItemSelected = (item == selectedItem);
                    boolean isItemHovered = isPointInDropdownItem(new Point(width / 2, yPos + itemHeight / 2));

                    // Draw item background
                    if (isItemSelected) {
                        renderer.setColor(new Color(180, 200, 255));
                    } else if (isItemHovered) {
                        renderer.setColor(hoverColor);
                    } else {
                        renderer.setColor(backgroundColor);
                    }
                    renderer.fillRect(0, yPos, width, itemHeight);

                    // Draw item text
                    renderer.setColor(textColor);
                    drawTextCentered(renderer, item.toString(), 5, yPos, width - 10, itemHeight);

                    // Draw separator
                    renderer.setColor(borderColor);
                    renderer.drawLine(0, yPos + itemHeight, width, yPos + itemHeight);
                }
                yPos += itemHeight;
            }

            // Restore original apply and clip
            renderer.popTransform();
            renderer.popClip();
//            renderer.setTransform(originalTransform);
//            renderer.setClip(originalClip);
        }
    }

    private void drawTextCentered(Renderer g, String text, int x, int y, int width, int height) {
        var fm = g.getFontInfo();
        int textX = x + (width - fm.getStringWidth(text)) / 2;
        int textY = y + (height + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(text, textX, textY);
    }

    private void drawDropdownArrow(Renderer g, int x, int y, int size, boolean pointUp) {
        int[] xPoints = {x - size / 2, x, x + size / 2};
        int[] yPoints;

        if (pointUp) {
            yPoints = new int[]{y + size / 3, y - size / 3, y + size / 3};
        } else {
            yPoints = new int[]{y - size / 3, y + size / 3, y - size / 3};
        }

        g.setColor(Color.BLACK);
        g.fillPolygon(xPoints, yPoints, 3);
    }

    private void toggleDropdown() {
        // If we're already the active dropdown, just toggle state
        if (this == activeDropdown) {
            isExpanded = !isExpanded;
            if (!isExpanded) {
                activeDropdown = null;
                GuiMouseManager.releaseMouseCapture();
            }
        }
        // If another dropdown is active, close it first
        else if (activeDropdown != null) {
            activeDropdown.isExpanded = false;
            isExpanded = true;
            activeDropdown = this;
        }
        // No active dropdown, expand this one
        else {
            isExpanded = true;
            activeDropdown = this;
            GuiMouseManager.setMouseCapture(this);
        }
    }

    private int getItemIndexAtPoint(Point point) {
        if (point.y >= height) {
            int relativeY = point.y - height + scrollOffset;
            return relativeY / itemHeight;
        }
        return -1;
    }

    private boolean isPointInDropdownItem(Point point) {
        int itemIndex = getItemIndexAtPoint(point);
        return itemIndex >= 0 && itemIndex < items.size();
    }

    @Override
    public GuiEventState processMouseEvent(MouseEvent e) {
        Point localPoint = transformToLocalSpace(e.getPoint());
        boolean wasPress = e instanceof MouseEvent.Pressed || e instanceof MouseEvent.Clicked || e instanceof MouseEvent.Released;
        boolean wasInSelectionArea = new Rectangle(0, height, width, Math.min(maxDropdownHeight, items.size() * itemHeight)).contains(localPoint);
        boolean wasInToggleArea = new Rectangle(0, 0, width, height).contains(localPoint);

        // BUG: If we press outside the dropdown area and onto a button it will both click the button and close the dropdown
        // BUG: If we select a dropdown option and are over a component that component will not receive an onHover event
        // BUG: If the dropdown is expanded, we can still interact with non-dropdown components with non-press events

        // Clicked outside the dropdown when open, close the dropdown
        if (isExpanded && !wasInToggleArea && !wasInSelectionArea && wasPress) {
            // If clicking outside, close dropdown
            toggleDropdown();
            GuiMouseManager.releaseMouseCapture();
            return GuiEventState.CONSUMED; // Let other components handle the click
        }

        // Clicking the toggle area, open/close the dropdown
        if (wasInToggleArea && !wasInSelectionArea && wasPress) {
            toggleDropdown();
            return GuiEventState.CONSUMED;
        }

        // Clicked on a dropdown option
        // NOTE: I feel like we should include isExpanded in this check, but it seems to work without it
        if (wasInSelectionArea && !wasInToggleArea && wasPress) {
            // Check if click was on an item
            int itemIndex = getItemIndexAtPoint(localPoint);
            // Select item
            T previousSelection = selectedItem;
            selectedItem = items.get(itemIndex);

            // Hide dropdown
            toggleDropdown();
            GuiMouseManager.releaseMouseCapture();

            // Trigger callback if selection changed
            if (onSelectionChanged != null && selectedItem != previousSelection) {
                onSelectionChanged.accept(selectedItem);
            }

            return GuiEventState.CONSUMED;
        }


        // Default handling in the case of non-press interaction with the toggle area
        if (!isExpanded && wasInToggleArea && !wasInSelectionArea && !wasPress) {
            return super.processMouseEvent(e);
        }

        return GuiEventState.NOT_CONSUMED;
    }

    @Override
    protected boolean containsPoint(Point point) {
        // When expanded, the dropdown is larger than its normal bounds
        if (isExpanded) {
            int dropdownHeight = Math.min(maxDropdownHeight, items.size() * itemHeight);
            return point.x >= 0 && point.x < width &&
                    point.y >= 0 && point.y < (height + dropdownHeight);
        }
        // Normal bounds check otherwise
        return super.containsPoint(point);
    }
}