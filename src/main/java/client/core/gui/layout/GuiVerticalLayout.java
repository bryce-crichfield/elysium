package client.core.gui.layout;


import client.core.gui.GuiComponent;
import client.core.gui.container.GuiContainer;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GuiVerticalLayout implements GuiLayout {
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

        // First calculate total height of all children + spacing
        int totalChildrenHeight = 0;
        for (var child : children) {
            if (child.isVisible()) {
                totalChildrenHeight += child.getHeight();
                if (totalChildrenHeight > 0) {
                    // Add spacing after each child except the last one
                    totalChildrenHeight += spacing;
                }
            }
        }
        // Remove the last spacing if we added it
        if (totalChildrenHeight > 0 && children.size() > 1) {
            totalChildrenHeight -= spacing;
        }

        // Calculate starting Y position based on justification
        int startY;
        switch (justify) {
            case START:
                startY = padding;
                break;
            case CENTER:
                startY = padding + (availableHeight - totalChildrenHeight) / 2;
                break;
            case END:
                startY = padding + availableHeight - totalChildrenHeight;
                break;
            case SPACE_BETWEEN:
                startY = padding;
                // We'll handle SPACE_BETWEEN separately later
                break;
            case SPACE_AROUND:
                // We'll handle SPACE_AROUND separately later
                startY = padding;
                break;
            default:
                startY = padding;
                break;
        }

        // Calculate actual spacing for SPACE_BETWEEN and SPACE_AROUND
        int effectiveSpacing = spacing;
        if (children.size() > 1) {
            if (justify == GuiJustification.SPACE_BETWEEN) {
                effectiveSpacing = (availableHeight - getTotalChildrenHeight(children)) / (children.size() - 1);
            } else if (justify == GuiJustification.SPACE_AROUND) {
                effectiveSpacing = (availableHeight - getTotalChildrenHeight(children)) / (children.size() * 2);
                startY = padding + effectiveSpacing; // Start after first margin
            }
        }

        // Position each child
        int currentY = startY;
        for (var child : children) {
            if (!child.isVisible()) {
                continue;
            }

            // Calculate X position based on horizontal alignment
            int childX;
            switch (alignment) {
                case START:
                    childX = padding;
                    break;
                case CENTER:
                    childX = padding + (availableWidth - child.getWidth()) / 2;
                    break;
                case END:
                    childX = padding + availableWidth - child.getWidth();
                    break;
                default:
                    childX = padding;
                    break;
            }

            // Set the position
            child.setPosition(childX, currentY);

            // Move to next position
            currentY += child.getHeight() + (justify == GuiJustification.SPACE_AROUND ?
                    effectiveSpacing * 2 : effectiveSpacing);
        }
    }

    private int getTotalChildrenHeight(List<GuiComponent> children) {
        int totalHeight = 0;
        for (GuiComponent child : children) {
            if (child.isVisible()) {
                totalHeight += child.getHeight();
            }
        }
        return totalHeight;
    }
}