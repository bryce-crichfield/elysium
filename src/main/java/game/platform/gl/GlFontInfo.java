package game.platform.gl;

import game.platform.FontInfo;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;

// Simple value class for font data
public class GlFontInfo implements FontInfo {
    private final int textureId;
    private final STBTTPackedchar.Buffer charData;
    private final int bitmapWidth;
    private final int bitmapHeight;
    private final float scale;
    private final float ascent;
    private final float descent;
    private final float lineGap;
    private final float lineHeight;

    public GlFontInfo(int textureId, STBTTPackedchar.Buffer charData,
                      int bitmapWidth, int bitmapHeight,
                      float scale, float ascent, float descent, float lineGap) {
        this.textureId = textureId;
        this.charData = charData;
        this.bitmapWidth = bitmapWidth;
        this.bitmapHeight = bitmapHeight;
        this.scale = scale;
        this.ascent = ascent;
        this.descent = descent;
        this.lineGap = lineGap;
        this.lineHeight = ascent - descent + lineGap;
    }

    // Simple getters
    public int getTextureId() { return textureId; }
    public STBTTPackedchar.Buffer getCharData() { return charData; }
    public int getBitmapWidth() { return bitmapWidth; }
    public int getBitmapHeight() { return bitmapHeight; }
    public float getScale() { return scale; }

    // FontInfo implementation
    @Override
    public int getHeight() { return (int) lineHeight; }

    @Override
    public int getAscent() { return (int) ascent; }

    @Override
    public int getDescent() { return (int) -descent; }

    @Override
    public int getLeading() { return (int) lineGap; }

    @Override
    public int stringWidth(String str) {
        if (str == null || str.isEmpty()) return 0;

        float width = 0;

        try (MemoryStack stack = stackPush()) {
            IntBuffer pCodePoint = stack.mallocInt(1);
            FloatBuffer xpos = stack.floats(0.0f);
            FloatBuffer ypos = stack.floats(0.0f);
            STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);

            for (int i = 0; i < str.length(); ) {
                i += getCP(str, i, pCodePoint);
                int cp = pCodePoint.get(0);

                if (cp == '\n') continue;
                if (cp < 32 || cp > 127) cp = '?';

                stbtt_GetPackedQuad(charData, bitmapWidth, bitmapHeight, cp - 32, xpos, ypos, q, false);
            }

            width = xpos.get(0);
        }

        return (int) width;
    }

    // Helper method to handle UTF-8 character decoding
    static int getCP(String text, int i, IntBuffer cpOut) {
        char c1 = text.charAt(i);
        if (Character.isHighSurrogate(c1) && i + 1 < text.length()) {
            char c2 = text.charAt(i + 1);
            if (Character.isLowSurrogate(c2)) {
                cpOut.put(0, Character.toCodePoint(c1, c2));
                return 2;
            }
        }
        cpOut.put(0, c1);
        return 1;
    }

    // Free resources when done
    public void dispose() {
        if (charData != null) {
            charData.free();
        }
        GL11.glDeleteTextures(textureId);
    }
}