package game.gui.container;

import game.gui.GuiComponent;
import game.gui.input.GuiEventState;
import game.gui.layout.GuiNullLayout;
import game.gui.style.GuiStyle;
import game.input.MouseEvent;
import game.gui.layout.GuiLayout;
import game.gui.style.GuiBackground;
import game.gui.style.GuiBorder;
import game.graphics.Renderer;
import lombok.Getter;
import lombok.Setter;

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
//        if (background != null) {
//            background.render(renderer, width, height, 0);
//        }
//
//        if (border != null) {
//            border.render(renderer, width, height, 0);
//        }

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
