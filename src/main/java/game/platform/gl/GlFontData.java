package game.platform.gl;

import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;

import java.nio.ByteBuffer;

/**
 * Stores information about a loaded font
 */
public class GlFontData  {
    private ByteBuffer fontBuffer;
    private STBTTFontinfo fontInfo;
    private float scale;
    private int ascent;
    private int descent;
    private int lineGap;
    private int height;
    private String name;
    private int size;

    public GlFontData(ByteBuffer fontBuffer, String name, int size) {
        this.fontBuffer = fontBuffer;
        this.name = name;
        this.size = size;

        // Initialize font info
        fontInfo = STBTTFontinfo.create();
        if (!STBTruetype.stbtt_InitFont(fontInfo, fontBuffer)) {
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
        lineGap = (int) (lineGapBuffer[0] * scale);
        height = ascent - descent + lineGap;
    }

    // Getters
    public ByteBuffer getFontBuffer() { return fontBuffer; }
    public STBTTFontinfo getFontInfo() { return fontInfo; }
    public float getScale() { return scale; }
    public int getAscent() { return ascent; }
    public int getDescent() { return descent; }
    public int getLineGap() { return lineGap; }
    public int getHeight() { return height; }
    public String getName() { return name; }
    public int getSize() { return size; }
}