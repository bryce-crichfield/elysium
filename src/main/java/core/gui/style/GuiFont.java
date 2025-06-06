package core.gui.style;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import core.graphics.Renderer;
import core.graphics.font.FontInfo;
import core.gui.layout.GuiAlignment;
import core.gui.layout.GuiJustification;
import lombok.Getter;

import java.awt.*;
import java.util.Optional;

@Getter
public class GuiFont {
    private final String name;
    private final int size;
    private final Color color;
    private final GuiAlignment alignment = GuiAlignment.CENTER;
    private final GuiJustification justification = GuiJustification.CENTER;

    public GuiFont(Color color, String name, int size) {
        this.color = color;
        this.name = name;
        this.size = size;
    }

    public static Optional<GuiFont> deserialize(JsonObject json) {
        try {
            JsonArray colorArray = json.getAsJsonArray("color");
            Color color = new Color(colorArray.get(0).getAsInt(), colorArray.get(1).getAsInt(), colorArray.get(2).getAsInt(), colorArray.get(3).getAsInt());

            String name = json.get("name").getAsString();
            int size = json.get("size").getAsInt();

            return Optional.of(new GuiFont(color, name, size));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void render(Renderer renderer, String text, int width, int height) {
        renderer.setColor(this.color);

        FontInfo fontInfo = renderer.getFontInfo(name, size);
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

        renderer.setFont(name, size);
        renderer.drawString(text, textX, textY);
    }
}
