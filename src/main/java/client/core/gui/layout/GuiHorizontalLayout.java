package client.core.gui.layout;

import client.core.gui.GuiComponent;
import client.core.gui.container.GuiContainer;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuiHorizontalLayout implements GuiLayout {
  private GuiAlignment alignment = GuiAlignment.CENTER;
  private GuiJustification justify = GuiJustification.START;
  private int padding = 0;
  private int spacing = 0;

  @Override
  public void onLayout(GuiContainer parent) {
    List<GuiComponent> children = parent.getChildren();
    if (children.isEmpty()) {
      return;
    }

    int parentWidth = parent.getWidth();
    int parentHeight = parent.getHeight();
    int availableWidth = parentWidth - (padding * 2);
    int availableHeight = parentHeight - (padding * 2);

    // First calculate total width of all children + spacing
    int totalChildrenWidth = 0;
    for (var child : children) {
      if (child.isVisible()) {
        totalChildrenWidth += child.getWidth();
        if (totalChildrenWidth > 0) {
          // Add spacing after each child except the last one
          totalChildrenWidth += spacing;
        }
      }
    }
    // Remove the last spacing if we added it
    if (totalChildrenWidth > 0 && children.size() > 1) {
      totalChildrenWidth -= spacing;
    }

    // Calculate starting X position based on justification
    int startX;
    switch (justify) {
      case START:
        startX = padding;
        break;
      case CENTER:
        startX = padding + (availableWidth - totalChildrenWidth) / 2;
        break;
      case END:
        startX = padding + availableWidth - totalChildrenWidth;
        break;
      case SPACE_BETWEEN:
        startX = padding;
        // We'll handle SPACE_BETWEEN separately later
        break;
      case SPACE_AROUND:
        // We'll handle SPACE_AROUND separately later
        startX = padding;
        break;
      default:
        startX = padding;
        break;
    }

    // Calculate actual spacing for SPACE_BETWEEN and SPACE_AROUND
    int effectiveSpacing = spacing;
    if (children.size() > 1) {
      if (justify == GuiJustification.SPACE_BETWEEN) {
        effectiveSpacing =
            (availableWidth - getTotalChildrenWidth(children)) / (children.size() - 1);
      } else if (justify == GuiJustification.SPACE_AROUND) {
        effectiveSpacing =
            (availableWidth - getTotalChildrenWidth(children)) / (children.size() * 2);
        startX = padding + effectiveSpacing; // Start after first margin
      }
    }

    // Position each child
    int currentX = startX;
    for (var child : children) {
      if (!child.isVisible()) {
        continue;
      }

      // Calculate Y position based on vertical alignment
      int childY;
      switch (alignment) {
        case START:
          childY = padding;
          break;
        case CENTER:
          childY = padding + (availableHeight - child.getHeight()) / 2;
          break;
        case END:
          childY = padding + availableHeight - child.getHeight();
          break;
        default:
          childY = padding;
          break;
      }

      // Set the position
      child.setPosition(currentX, childY);

      // Move to next position
      currentX +=
          child.getWidth()
              + (justify == GuiJustification.SPACE_AROUND
                  ? effectiveSpacing * 2
                  : effectiveSpacing);
    }
  }

  private int getTotalChildrenWidth(List<GuiComponent> children) {
    int totalWidth = 0;
    for (GuiComponent child : children) {
      if (child.isVisible()) {
        totalWidth += child.getWidth();
      }
    }
    return totalWidth;
  }
}
