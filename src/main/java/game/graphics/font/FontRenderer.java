package game.graphics.font;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTruetype;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class FontRenderer {
    private static final int BITMAP_WIDTH = 512*4;
    private static final int BITMAP_HEIGHT = 512*4;
    static final int FIRST_CHAR = 32; // Space character
    static final int NUM_CHARS = 96; // Basic Latin characters

    // Static cache for loaded fonts and resources
    private static final Map<String, FontInfo> fontCache = new HashMap<>();
    private static final Map<String, ByteBuffer> bitmapCache = new HashMap<>();
    private static final Map<String, STBTTBakedChar.Buffer> charDataCache = new HashMap<>();
    private static final Map<String, Integer> textureCache = new HashMap<>();
    private static final Map<String, ByteBuffer> fontBufferCache = new HashMap<>();

    // Current font properties
    private FontInfo currentFont;
    private STBTTBakedChar.Buffer charData;
    private int textureId;

    public FontRenderer() {
        // Default initialization
        setFont("/fonts/arial", 16);
    }

    public void setFont(String name, int size) {
        String fontKey = name + "_" + size;

        // If font is already cached, just retrieve it
        if (fontCache.containsKey(fontKey)) {
            currentFont = fontCache.get(fontKey);
            charData = charDataCache.get(fontKey);
            textureId = textureCache.get(fontKey);
            return;
        }

        // Load and initialize a new font
        ByteBuffer fontBuffer = FontLoader.loadFont(name + ".ttf");
        FontInfo fontData = new FontInfo(fontBuffer, size);

        // Store font buffer to prevent garbage collection
        fontBufferCache.put(fontKey, fontBuffer);

        // Create bitmap for the font
        ByteBuffer bitmap = BufferUtils.createByteBuffer(BITMAP_WIDTH * BITMAP_HEIGHT);
        charData = STBTTBakedChar.malloc(NUM_CHARS);

        // Bake the font to the bitmap
        STBTruetype.stbtt_BakeFontBitmap(
                fontBuffer,
                size,
                bitmap,
                BITMAP_WIDTH,
                BITMAP_HEIGHT,
                FIRST_CHAR,
                charData
        );

        // Create OpenGL texture for the bitmap
        textureId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                GL11.GL_ALPHA,
                BITMAP_WIDTH,
                BITMAP_HEIGHT,
                0,
                GL11.GL_ALPHA,
                GL11.GL_UNSIGNED_BYTE,
                bitmap
        );
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

        // Cache all resources
        fontCache.put(fontKey, fontData);
        bitmapCache.put(fontKey, bitmap);
        charDataCache.put(fontKey, charData);
        textureCache.put(fontKey, textureId);

        // Set current font properties
        currentFont = fontData;
    }

    public FontInfo getFontInfo() {
        return currentFont;
    }

    public FontInfo getFontInfo(String name) {
        String fontKey = name + "_" + currentFont.getSize();
        return fontCache.get(fontKey);
    }

    public void drawString(String text, int x, int y, Color color) {
        // Enable blending and textures
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

        // Bind the font texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

        // Set the text color
        GL11.glColor4f(
                color.getRed() / 255.0f,
                color.getGreen() / 255.0f,
                color.getBlue() / 255.0f,
                color.getAlpha() / 255.0f
        );

        // Now try to render the text using STB
        FloatBuffer xBuffer = BufferUtils.createFloatBuffer(1);
        FloatBuffer yBuffer = BufferUtils.createFloatBuffer(1);
        STBTTAlignedQuad quad = STBTTAlignedQuad.malloc();

        xBuffer.put(0, 0);
        yBuffer.put(0, 0);

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            // Skip characters outside our drawable range
            if (c < FIRST_CHAR || c >= FIRST_CHAR + NUM_CHARS) {
                continue;
            }

            // Get quad for the character
            STBTruetype.stbtt_GetBakedQuad(
                    charData,
                    BITMAP_WIDTH,
                    BITMAP_HEIGHT,
                    c - FIRST_CHAR,
                    xBuffer,
                    yBuffer,
                    quad,
                    true
            );

            // Draw the quad with its original coordinates
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(quad.s0(), quad.t0());
            GL11.glVertex2f((float) x + quad.x0(), y + quad.y0());
            GL11.glTexCoord2f(quad.s1(), quad.t0());
            GL11.glVertex2f((float) x + quad.x1(), y + quad.y0());
            GL11.glTexCoord2f(quad.s1(), quad.t1());
            GL11.glVertex2f((float) x + quad.x1(), y + quad.y1());
            GL11.glTexCoord2f(quad.s0(), quad.t1());
            GL11.glVertex2f((float) x + quad.x0(), y + quad.y1());
            GL11.glEnd();
        }

        quad.free();

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }
}