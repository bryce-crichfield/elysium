package game.gui.control;

import game.graphics.font.FontInfo;
import game.gui.GuiComponent;
import game.graphics.Renderer;
import game.gui.style.GuiFont;
import game.gui.style.GuiTheme;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

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
        style.getFont().render(renderer, text, getWidth(), getHeight());
    }
}
