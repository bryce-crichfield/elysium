package game.gui.style;

import game.graphics.Renderer;
import game.graphics.font.FontInfo;
import game.gui.layout.GuiAlignment;
import game.gui.layout.GuiJustification;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

@Getter
public class GuiFont {
    private Color color;
    private final String name;
    private final int size;
    private GuiAlignment alignment = GuiAlignment.CENTER;
    private GuiJustification justification = GuiJustification.CENTER;

    public GuiFont(Color color, String name, int size) {
        this.color = color;
        this.name = name;
        this.size = size;
    }

    public void render(Renderer renderer, String text, int width, int height) {
        renderer.setColor(Color.WHITE);

        FontInfo fontInfo = renderer.getFontInfo();
        int textWidth = fontInfo.getStringWidth(text);
        int textHeight = fontInfo.getHeight();

        int textX = switch (alignment) {
            case GuiAlignment.START -> 0;
            case CENTER -> (width - textWidth) / 2;
            case GuiAlignment.END -> width - textWidth;
        };


        int textY = switch (justification) {
            case GuiJustification.CENTER -> (height - textHeight) / 2 + fontInfo.getAscent();
            case GuiJustification.END -> height - textHeight + fontInfo.getAscent();
            default -> 0;
        };

        renderer.drawString(text, textX, textY);
    }
}
