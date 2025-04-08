package game.gui;

import game.gui.layout.GuiLayout;
import game.gui.layout.GuiVerticalLayout;
import game.gui.style.GuiBackground;
import game.gui.style.GuiBorder;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class GuiContainer extends GuiComponent {
    @Getter
    protected List<GuiComponent> children = new ArrayList<>();
    protected GuiLayout layout = new GuiVerticalLayout();

    @Getter
    @Setter
    protected GuiBackground background = null;

    @Getter
    @Setter
    protected GuiBorder border = null;

    public GuiContainer(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public void addChild(GuiComponent child) {
        children.add(child);
        child.parent = this;
        layout.onLayout(this);
    }

    @Override
    protected void onRender(Graphics2D g) {
        // Apply clipping based on overflow setting
        Shape originalClip = g.getClip();
        g.setClip(new Rectangle(0, 0, width, height));

        if (background != null) {
            background.render(g, width, height, 0);
        }

        if (border != null) {
            border.render(g, width, height, 0);
        }

        // Render all children
        for (GuiComponent child : children) {
            child.render(g);
        }

        // Restore clip
        if (originalClip != null) {
            g.setClip(originalClip);
        }
    }

    @Override
    protected void onUpdate(Duration delta) {
        for (var child : children) {
            child.update(delta);
        }
    }

    @Override
    protected final boolean onMouseEvent(MouseEvent e) {
        // Propagate backwards because insertion order implicitly defines z-order
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).processMouseEvent(e)) {
                return true;
            }
        }

        return false;
    }

    public void setLayout(GuiLayout guiLayout) {
        this.layout = guiLayout;
        layout.onLayout(this);
    }


    // Other methods for child management, etc.
}
