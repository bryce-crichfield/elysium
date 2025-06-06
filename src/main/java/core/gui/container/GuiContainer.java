package core.gui.container;

import core.gui.GuiComponent;
import core.gui.input.GuiEventState;
import core.gui.layout.GuiNullLayout;
import core.gui.style.GuiStyle;
import core.input.MouseEvent;
import core.gui.layout.GuiLayout;
import core.graphics.Renderer;
import lombok.Getter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GuiContainer extends GuiComponent {
    @Getter
    protected List<GuiComponent> children = new ArrayList<>();

    @Getter
    protected GuiLayout layout = new GuiNullLayout();

    public GuiContainer(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public void addChild(GuiComponent child) {
        children.add(child);
        child.setParent(this);
        layout.onLayout(this);
    }

    @Override
    protected void onRender(Renderer renderer) {
        renderer.pushClip(0, 0, width, height);

        // Render background and border
        if (style != null) {
            style.getBackground().render(renderer, width, height, 0);
            style.getBorder().render(renderer, width, height, 0);
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

    @Override
    protected String getComponentName() {
        return "container";
    }

    @Override
    public void applyStyle(Map<String, GuiStyle> styles) {
        super.applyStyle(styles);
        for (GuiComponent child : children) {
            child.applyStyle(styles);
        }
    }

    public void setLayout(GuiLayout guiLayout) {
        this.layout = guiLayout;
        layout.onLayout(this);
    }

    public void removeChild(GuiComponent child) {
        children.remove(child);
        child.setParent(null);
        layout.onLayout(this);
    }
}
