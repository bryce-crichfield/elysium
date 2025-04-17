package game.gui;

import game.gui.input.GuiEventState;
import game.gui.layout.GuiNullLayout;
import game.input.MouseEvent;
import game.gui.layout.GuiLayout;
import game.gui.layout.GuiVerticalLayout;
import game.gui.style.GuiBackground;
import game.gui.style.GuiBorder;
import game.graphics.Renderer;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class GuiContainer extends GuiComponent {
    @Getter
    protected List<GuiComponent> children = new ArrayList<>();

    @Getter
    protected GuiLayout layout = new GuiNullLayout();

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
    protected void onRender(Renderer renderer) {
        renderer.pushClip(0, 0, width, height);

        // Render background and border
        if (background != null) {
            background.render(renderer, width, height, 0);
        }

        if (border != null) {
            border.render(renderer, width, height, 0);
        }

        // Render children
        for (GuiComponent child : children) {
            child.render(renderer);
        }

        renderer.popClip();
    }

    @Override
    protected void onUpdate(Duration delta) {
        for (var child : children) {
            child.update(delta);
        }
    }

    @Override
    protected final GuiEventState onMouseEvent(MouseEvent e) {
        // Propagate backwards because insertion order implicitly defines z-order
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).processMouseEvent(e) == GuiEventState.CONSUMED) {
                return GuiEventState.CONSUMED;
            }
        }

        return GuiEventState.NOT_CONSUMED;
    }

    public void setLayout(GuiLayout guiLayout) {
        this.layout = guiLayout;
        layout.onLayout(this);
    }

    public void removeChild(GuiComponent child) {
        children.remove(child);
        child.parent = null;
        layout.onLayout(this);
    }


    // Other methods for child management, etc.
}
