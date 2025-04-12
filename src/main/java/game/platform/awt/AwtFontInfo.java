package game.platform.awt;

import game.platform.FontInfo;
import lombok.Value;

import java.awt.*;

@Value
public class AwtFontInfo implements FontInfo {
    private final FontMetrics fontMetrics;

    @Override
    public int stringWidth(String text) {
        return fontMetrics.stringWidth(text);
    }

    @Override
    public int getAscent() {
        return fontMetrics.getAscent();
    }

    @Override
    public int getDescent() {
        return fontMetrics.getDescent();
    }

    @Override
    public int getHeight() {
        return fontMetrics.getHeight();
    }
}
