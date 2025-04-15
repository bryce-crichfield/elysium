package game.gui.style;

import game.gui.GuiComponent;
import game.platform.Renderer;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public class GuiLabel extends GuiComponent {
    private String text;
    private Color color;
    private Font font;

    public GuiLabel(int width, int height, String text) {
        super(0, 0, width, height);
        this.text = text;
        this.color = Color.WHITE;
        this.font = new Font("/fonts/arial", Font.PLAIN, 12);
    }

//    public void insert(char c, int index) {
//        if (index < 0 || index > text.length()) {
//            throw new IndexOutOfBoundsException("Index out of bounds");
//        }
//        text = text.substring(0, index) + c + text.substring(index);
//    }
//
//    public void remove(int index) {
//        if (index < 0 || index >= text.length()) {
//            throw new IndexOutOfBoundsException("Index out of bounds");
//        }
//        text = text.substring(0, index) + text.substring(index + 1);
//    }

    @Override
    protected void onRender(Renderer g) {
        g.setColor(color);
        g.setFont(font);
        var metrics = g.getFontInfo(font);
        int textWidth = metrics.getStringWidth(text);
        int textHeight = metrics.getHeight();
        g.drawString(text, (width - textWidth) / 2, (height + textHeight) / 2);
    }
}
