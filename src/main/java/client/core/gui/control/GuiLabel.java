package client.core.gui.control;

import client.core.gui.GuiComponent;
import client.core.graphics.Renderer;
import client.core.gui.style.GuiTheme;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuiLabel extends GuiComponent {
    private String text;

    public GuiLabel(int width, int height, String text) {
        super(0, 0, width, height);
        this.text = text;
        style = GuiTheme.getInstance().label();
    }

    @Override
    protected void onRender(Renderer renderer) {
        if (style.getBackground() != null) {
            style.getBackground().render(renderer, getWidth(), getHeight(), 0);
        }

        if (style.getBorder() != null) {
            style.getBorder().render(renderer, getWidth(), getHeight(), 0);
        }


        style.getFont().render(renderer, text, getWidth(), getHeight());
    }

    @Override
    protected String getComponentName() {
        return "label";
    }
}
