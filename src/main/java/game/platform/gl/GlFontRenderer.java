package game.platform.gl;

import game.platform.FontInfo;
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

public class GlFontRenderer {
    private static final int BITMAP_WIDTH = 512*4;
    private static final int BITMAP_HEIGHT = 512*4;
    private static final int FIRST_CHAR = 32; // Space character
    private static final int NUM_CHARS = 96; // Basic Latin characters

    // Static cache for loaded fonts and resources
    private static Map<String, GlFontData> fontCache = new HashMap<>();
    private static Map<String, ByteBuffer> bitmapCache = new HashMap<>();
    private static Map<String, STBTTBakedChar.Buffer> charDataCache = new HashMap<>();
    private static Map<String, Integer> textureCache = new HashMap<>();

    // Current font properties
    private String currentFontName;
    private int currentFontSize;
    private GlFontData currentFont;
    private ByteBuffer bitmap;
    private STBTTBakedChar.Buffer charData;
    private int textureId;
    private FontInfoImpl fontInfo;

    public GlFontRenderer() {
        // Default initialization
        setFont("/fonts/arial", 16);
    }

    /**
     * Sets the current font
     * @param name Font name (file name without extension)
     * @param size Font size in pixels
     */
    public void setFont(String name, int size) {
        String fontKey = name + "_" + size;

        // Check if the font is already in the cache
        if (!fontCache.containsKey(fontKey)) {
            // Load the font data
            ByteBuffer fontBuffer = GlFontLoader.loadFont(name + ".ttf");
            GlFontData fontData = new GlFontData(fontBuffer, name, size);
            fontCache.put(fontKey, fontData);

            // Create bitmap for the font
            bitmap = BufferUtils.createByteBuffer(BITMAP_WIDTH * BITMAP_HEIGHT);
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

            bitmapCache.put(fontKey, bitmap);
            charDataCache.put(fontKey, charData);

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

            textureCache.put(fontKey, textureId);
        } else {
            // Retrieve from cache
            currentFont = fontCache.get(fontKey);
            bitmap = bitmapCache.get(fontKey);
            charData = charDataCache.get(fontKey);
            textureId = textureCache.get(fontKey);
        }

        currentFontName = name;
        currentFontSize = size;
        currentFont = fontCache.get(fontKey);
        fontInfo = new FontInfoImpl(currentFont);
    }

    public void drawDebugTexture(int x, int y, int width, int height) {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(x, y);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f(x + width, y);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f(x + width, y + height);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f(x, y + height);
        GL11.glEnd();

        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    /**
     * Gets information about the current font
     * @return FontInfo implementation with metrics for the current font
     */
    public FontInfo getFontInfo() {
        return fontInfo;
    }

    /**
     * Draws text at the specified position with the specified color
     * @param text Text to draw
     * @param x X position
     * @param y Y position
     * @param color Text color
     */
    public void drawString(String text, int x, int y, Color color) {
        // Debug info
        System.out.println("FontRenderer drawing: '" + text + "' at: " + x + ", " + y);

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

        // Draw a test quad to verify texture rendering works
        // This should draw a texture quad at the position
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0); GL11.glVertex2f(x, y);
        GL11.glTexCoord2f(1, 0); GL11.glVertex2f(x + 50, y);
        GL11.glTexCoord2f(1, 1); GL11.glVertex2f(x + 50, y + 50);
        GL11.glTexCoord2f(0, 1); GL11.glVertex2f(x, y + 50);
        GL11.glEnd();

        // Now try to render the text using STB
        FloatBuffer xBuffer = BufferUtils.createFloatBuffer(1);
        FloatBuffer yBuffer = BufferUtils.createFloatBuffer(1);
        STBTTAlignedQuad quad = STBTTAlignedQuad.malloc();

        xBuffer.put(0, 0);
        yBuffer.put(0, 0);

        float xPos = x;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            // Skip characters outside our range
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
            GL11.glVertex2f(xPos + quad.x0(), y + quad.y0());
            GL11.glTexCoord2f(quad.s1(), quad.t0());
            GL11.glVertex2f(xPos + quad.x1(), y + quad.y0());
            GL11.glTexCoord2f(quad.s1(), quad.t1());
            GL11.glVertex2f(xPos + quad.x1(), y + quad.y1());
            GL11.glTexCoord2f(quad.s0(), quad.t1());
            GL11.glVertex2f(xPos + quad.x0(), y + quad.y1());
            GL11.glEnd();
        }

        quad.free();

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    /**
     * Implementation of the FontInfo interface
     */
    private class FontInfoImpl implements FontInfo {
        private GlFontData fontData;

        public FontInfoImpl(GlFontData fontData) {
            this.fontData = fontData;
        }

        @Override
        public int getLeading() {
            return fontData.getLineGap();
        }

        @Override
        public int stringWidth(String text) {
            int width = 0;
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (c < FIRST_CHAR || c >= FIRST_CHAR + NUM_CHARS) {
                    continue;
                }

                int[] advanceWidth = new int[1];
                int[] leftSideBearing = new int[1];
                STBTruetype.stbtt_GetCodepointHMetrics(
                        fontData.getFontInfo(),
                        c,
                        advanceWidth,
                        leftSideBearing
                );

                width += (int) (advanceWidth[0] * fontData.getScale());

                // Add kerning if there's a next character
                if (i < text.length() - 1) {
                    width += (int) (STBTruetype.stbtt_GetCodepointKernAdvance(
                            fontData.getFontInfo(),
                            c,
                            text.charAt(i + 1)
                    ) * fontData.getScale());
                }
            }

            return width;
        }

        @Override
        public int getAscent() {
            return fontData.getAscent();
        }

        @Override
        public int getDescent() {
            return fontData.getDescent();
        }

        @Override
        public int getHeight() {
            return fontData.getHeight();
        }
    }
}