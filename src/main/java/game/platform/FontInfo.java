package game.platform;

import lombok.Getter;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static game.platform.FontRenderer.FIRST_CHAR;
import static game.platform.FontRenderer.NUM_CHARS;

@Getter
public class FontInfo {
    private final STBTTFontinfo fontInfo;
    private final float scale;
    private final int ascent;
    private final int descent;
    private final int leading;
    private final int height;
    private final int size;

    private final Map<String, Integer> widthCache = new HashMap<>();

    public FontInfo(ByteBuffer stbFontBuffer, int size) {
        this.size = size;

        // Initialize font info
        fontInfo = STBTTFontinfo.create();
        if (!STBTruetype.stbtt_InitFont(fontInfo, stbFontBuffer)) {
            throw new RuntimeException("Failed to initialize font");
        }

        // Calculate font scaling
        scale = STBTruetype.stbtt_ScaleForPixelHeight(fontInfo, size);

        // Get font metrics
        int[] ascentBuffer = new int[1];
        int[] descentBuffer = new int[1];
        int[] lineGapBuffer = new int[1];
        STBTruetype.stbtt_GetFontVMetrics(fontInfo, ascentBuffer, descentBuffer, lineGapBuffer);

        ascent = (int) (ascentBuffer[0] * scale);
        descent = (int) (descentBuffer[0] * scale);
        leading = (int) (lineGapBuffer[0] * scale);
        height = ascent - descent + leading;
    }

    public int getStringWidth(String text) {
        if (fontInfo == null || text == null || text.isEmpty()) {
            return 0;
        }

        // Use double for accumulation to avoid floating-point errors
        double width = 0;
        int[] advanceWidth = new int[1];
        int[] leftSideBearing = new int[1];

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c < FIRST_CHAR || c >= FIRST_CHAR + NUM_CHARS) {
                continue;
            }

            STBTruetype.stbtt_GetCodepointHMetrics(fontInfo, c, advanceWidth, leftSideBearing);
            width += advanceWidth[0] * scale;

            // Add kerning
            if (i < text.length() - 1) {
                width += STBTruetype.stbtt_GetCodepointKernAdvance(
                        fontInfo, c, text.charAt(i + 1)) * scale;
            }
        }

        return (int)Math.round(width);
    }
}