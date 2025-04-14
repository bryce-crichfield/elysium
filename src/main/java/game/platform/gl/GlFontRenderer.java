package game.platform.gl;

import game.platform.FontInfo;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class GlFontRenderer {
    private final Map<Font, GlFontInfo> fontCache = new HashMap<>();
    private Font currentFont;

    public GlFontRenderer() {
        // Default font
        currentFont = new Font("Arial", Font.PLAIN, 12);

        // Pre-load default font
        getOrLoadFont(currentFont);
    }

    public void setFont(Font font) {
        this.currentFont = font;
    }

    public FontInfo getFontMetrics() {
        return getFontMetrics(currentFont);
    }

    public FontInfo getFontMetrics(Font font) {
        return getOrLoadFont(font);
    }

    public void drawString(String text, int x, int y, Color color) {
        if (text == null || text.isEmpty()) return;

        GlFontInfo fontInfo = getOrLoadFont(currentFont);

        // Save GL state
        glPushAttrib(GL_ENABLE_BIT | GL_COLOR_BUFFER_BIT | GL_TEXTURE_BIT);

        // Enable blending and texturing
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_TEXTURE_2D);

        // Bind font texture
        glBindTexture(GL_TEXTURE_2D, fontInfo.getTextureId());

        // Set text color
        glColor4f(color.getRed() / 255f, color.getGreen() / 255f,
                color.getBlue() / 255f, color.getAlpha() / 255f);

        float xpos = x;
        float ypos = y;

        try (MemoryStack stack = stackPush()) {
            IntBuffer pCodePoint = stack.mallocInt(1);
            FloatBuffer xpos_p = stack.floats(xpos);
            FloatBuffer ypos_p = stack.floats(ypos);
            STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);

            STBTTPackedchar.Buffer charData = fontInfo.getCharData();
            int bitmapWidth = fontInfo.getBitmapWidth();
            int bitmapHeight = fontInfo.getBitmapHeight();

            for (int i = 0; i < text.length(); ) {
                i += GlFontInfo.getCP(text, i, pCodePoint);
                int cp = pCodePoint.get(0);

                if (cp == '\n') {
                    ypos_p.put(0, ypos_p.get(0) + fontInfo.getHeight());
                    xpos_p.put(0, xpos);
                    continue;
                }

                if (cp < 32 || cp > 127) cp = '?';

                stbtt_GetPackedQuad(charData, bitmapWidth, bitmapHeight,
                        cp - 32, xpos_p, ypos_p, q, false);

                xpos = xpos_p.get(0);

                // Draw quad
                glBegin(GL_QUADS);
                glTexCoord2f(q.s0(), q.t0()); glVertex2f(q.x0(), q.y0());
                glTexCoord2f(q.s1(), q.t0()); glVertex2f(q.x1(), q.y0());
                glTexCoord2f(q.s1(), q.t1()); glVertex2f(q.x1(), q.y1());
                glTexCoord2f(q.s0(), q.t1()); glVertex2f(q.x0(), q.y1());
                glEnd();
            }
        }

        // Restore GL state
        glBindTexture(GL_TEXTURE_2D, 0);
        glPopAttrib();
    }

    private GlFontInfo getOrLoadFont(Font font) {
        GlFontInfo fontInfo = fontCache.get(font);

        if (fontInfo == null) {
            // Try to map font name to a resource path
            String fontPath = mapFontToPath(font);

            // Load the font
            fontInfo = GlFontLoader.loadFont(fontPath, font.getSize());

            // Cache it
            fontCache.put(font, fontInfo);
        }

        return fontInfo;
    }

    private String mapFontToPath(Font font) {
        String fontName = font.getName().toLowerCase();

        // Map common font names to resource paths
        if (fontName.contains("arial")) return "/fonts/arial.ttf";
        if (fontName.contains("times")) return "/fonts/times.ttf";
        if (fontName.contains("courier")) return "/fonts/courier.ttf";
        if (fontName.contains("verdana")) return "/fonts/verdana.ttf";

        // Default path
        return "/fonts/default.ttf";
    }

    public void dispose() {
        // Dispose all cached fonts
        for (GlFontInfo fontInfo : fontCache.values()) {
            fontInfo.dispose();
        }
        fontCache.clear();
    }
}