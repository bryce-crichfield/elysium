package client.core.gui.control;

import client.core.graphics.Renderer;
import client.core.gui.GuiComponent;
import client.core.gui.input.GuiEventState;
import client.core.gui.manager.GuiMouseCaptureManager;
import client.core.input.MouseEvent;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;

public class GuiDropdown<T> extends GuiComponent {
  // Main dropdown properties
  private final List<T> items = new ArrayList<>();
  @Getter @Setter private T selectedItem = null;

  // Styling properties
  @Getter @Setter private Color backgroundColor = new Color(240, 240, 240);
  @Getter @Setter private Color hoverColor = new Color(220, 220, 220);
  @Getter @Setter private Color textColor = Color.BLACK;
  @Getter @Setter private Color borderColor = new Color(200, 200, 200);

  private DropdownState state = DropdownState.COLLAPSED;
  private DropdownHandler<T> handler = new CollapsedHandler<>();

  // Item height for the dropdown list
  private final int itemHeight = 24;

  // The max height of the dropdown list when expanded
  private final int maxDropdownHeight = 150;

  // Selection callback
  private Consumer<T> onSelectionChanged = null;

  // Scrolling state for the dropdown list
  private final int scrollOffset = 0;

  // Z-order management - dropdowns should be rendered above other components
  private static final GuiDropdown<?> activeDropdown = null;

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
    drawDropdownArrow(renderer, width - 15, height / 2, 8, state == DropdownState.EXPANDED);

    // Draw dropdown menu if expanded

    if (state == DropdownState.EXPANDED) {
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

      // Draw dropdown items
      int yPos = height - scrollOffset;
      for (int i = 0; i < items.size(); i++) {
        T item = items.get(i);
        if (yPos >= height && yPos < height + dropdownListHeight) {
          // Check if this item is selected
          boolean isItemSelected = (item == selectedItem);
          boolean isItemHovered =
              isPointInDropdownItem(new Point(width / 2, yPos + itemHeight / 2));

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
      yPoints = new int[] {y + size / 3, y - size / 3, y + size / 3};
    } else {
      yPoints = new int[] {y - size / 3, y + size / 3, y - size / 3};
    }

    g.setColor(Color.BLACK);
    g.fillPolygon(xPoints, yPoints, 3);
  }

  private void toggleDropdown() {
    if (state == DropdownState.EXPANDED) {
      state = DropdownState.COLLAPSED;
      handler = new CollapsedHandler<>();
      GuiMouseCaptureManager.getInstance().releaseMouseCapture();
      return;
    }

    if (state == DropdownState.COLLAPSED) {
      state = DropdownState.EXPANDED;
      handler = new ExpandedHandler<>();
      GuiMouseCaptureManager.getInstance().setMouseCapture(this);
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
    Point localPoint = new Point(e.getX() - x, e.getY() - y);
    return handler.handleMouseEvent(this, e, localPoint);
  }

  @Override
  protected boolean containsPoint(Point point) {
    // When expanded, the dropdown is larger than its normal bounds
    if (state == DropdownState.EXPANDED) {
      int dropdownHeight = Math.min(maxDropdownHeight, items.size() * itemHeight);
      return point.x >= 0 && point.x < width && point.y >= 0 && point.y < (height + dropdownHeight);
    }
    // Normal bounds check otherwise
    return super.containsPoint(point);
  }

  @Override
  protected String getComponentName() {
    return "dropdown";
  }

  protected GuiEventState processBaseMouseEvent(MouseEvent e) {
    return super.processMouseEvent(e);
  }

  private boolean isPointInToggleArea(Point localPoint) {
    return new Rectangle(0, 0, width, height).contains(localPoint);
  }

  private void selectItemAtPoint(Point localPoint) {
    int itemIndex = getItemIndexAtPoint(localPoint);
    T previousSelection = selectedItem;
    selectedItem = items.get(itemIndex);

    if (onSelectionChanged != null && selectedItem != previousSelection) {
      onSelectionChanged.accept(selectedItem);
    }
  }

  private boolean isPointInSelectionArea(Point localPoint) {
    return new Rectangle(0, height, width, Math.min(maxDropdownHeight, items.size() * itemHeight))
        .contains(localPoint);
  }

  private enum DropdownState {
    COLLAPSED,
    EXPANDED
  }

  @FunctionalInterface
  private interface DropdownHandler<T> {
    GuiEventState handleMouseEvent(GuiDropdown<T> self, MouseEvent e, Point localPoint);
  }

  private static class CollapsedHandler<T> implements DropdownHandler<T> {
    @Override
    public GuiEventState handleMouseEvent(GuiDropdown<T> self, MouseEvent e, Point localPoint) {
      boolean wasPress = e instanceof MouseEvent.Pressed;
      boolean wasInToggleArea = self.isPointInToggleArea(localPoint);

      if (wasInToggleArea && wasPress) {
        self.toggleDropdown(); // Transitions to EXPANDED state
        return GuiEventState.CONSUMED;
      }

      if (wasInToggleArea) {
        //                return self.processBaseMouseEvent(e);
      }

      return GuiEventState.NOT_CONSUMED;
    }
  }

  private static class ExpandedHandler<T> implements DropdownHandler<T> {

    @Override
    public GuiEventState handleMouseEvent(GuiDropdown<T> self, MouseEvent e, Point localPoint) {
      boolean wasPress = e instanceof MouseEvent.Pressed;
      boolean wasInSelectionArea = self.isPointInSelectionArea(localPoint);
      boolean wasInToggleArea = self.isPointInToggleArea(localPoint);

      if (!wasInToggleArea && !wasInSelectionArea && wasPress) {
        self.toggleDropdown(); // Transitions to COLLAPSED state
        return GuiEventState.CONSUMED;
      }

      if (wasInToggleArea && wasPress) {
        self.toggleDropdown(); // Transitions to COLLAPSED state
        return GuiEventState.CONSUMED;
      }

      if (wasInSelectionArea && wasPress) {
        self.selectItemAtPoint(localPoint);
        self.toggleDropdown(); // Transitions to COLLAPSED state
        return GuiEventState.CONSUMED;
      }

      return GuiEventState.CONSUMED;
    }
  }
}
