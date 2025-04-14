package game.platform.gl;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;

// Separate loader class for font creation
public class GlFontLoader {
    // Create a font from a TTF file loaded as a resource
    public static GlFontInfo loadFont(String fontPath, int fontSize) {
        ByteBuffer ttfData = loadFontData(fontPath);
        if (ttfData == null) {
            System.err.println("Failed to load font data from: " + fontPath);
            return createFallbackFont(fontSize);
        }

        return createFont(ttfData, fontSize);
    }

    // Create a font from font data
    public static GlFontInfo createFont(ByteBuffer ttfData, int fontSize) {
        // Validate font data
        if (ttfData == null || ttfData.capacity() == 0) {
            System.err.println("Invalid font data");
            return createFallbackFont(fontSize);
        }

        // Reset buffer position
        ttfData.position(0);

        // Check if this is actually a valid font
        try (MemoryStack stack = stackPush()) {
            STBTTFontinfo info = STBTTFontinfo.malloc(stack);
            if (!stbtt_InitFont(info, ttfData)) {
                System.err.println("Not a valid font file");
                return createFallbackFont(fontSize);
            }
        } catch (Exception e) {
            System.err.println("Error validating font: " + e.getMessage());
            return createFallbackFont(fontSize);
        }

        // Font is valid, create the bitmap
        int bitmapW = 512;
        int bitmapH = 512;
        ByteBuffer bitmap = null;
        STBTTPackedchar.Buffer charData = null;
        int textureId = 0;

        try {
            // Allocate bitmap
            bitmap = BufferUtils.createByteBuffer(bitmapW * bitmapH);
            // Allocate character data for ASCII 32-127
            charData = STBTTPackedchar.malloc(96);

            // Reset buffer position again
            ttfData.position(0);

            try (STBTTPackContext pc = STBTTPackContext.malloc()) {
                if (!stbtt_PackBegin(pc, bitmap, bitmapW, bitmapH, 0, 1, 0L)) {
                    throw new RuntimeException("Failed to initialize font packing");
                }

                stbtt_PackSetOversampling(pc, 2, 2);

                if (!stbtt_PackFontRange(pc, ttfData, 0, fontSize, 32, charData)) {
                    throw new RuntimeException("Failed to pack font");
                }

                stbtt_PackEnd(pc);
            }

            // Create texture
            textureId = glGenTextures();
            // In GlFontLoader.createFont()
            glBindTexture(GL_TEXTURE_2D, textureId);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, bitmapW, bitmapH, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

            // Calculate metrics
            float scale = 0;
            float ascent = 0;
            float descent = 0;
            float lineGap = 0;

            ttfData.position(0);
            try (MemoryStack stack = stackPush()) {
                STBTTFontinfo info = STBTTFontinfo.malloc(stack);
                stbtt_InitFont(info, ttfData);

                scale = stbtt_ScaleForPixelHeight(info, fontSize);

                IntBuffer ascentBuffer = stack.mallocInt(1);
                IntBuffer descentBuffer = stack.mallocInt(1);
                IntBuffer lineGapBuffer = stack.mallocInt(1);

                stbtt_GetFontVMetrics(info, ascentBuffer, descentBuffer, lineGapBuffer);

                ascent = ascentBuffer.get(0) * scale;
                descent = descentBuffer.get(0) * scale;
                lineGap = lineGapBuffer.get(0) * scale;
            }

            return new GlFontInfo(textureId, charData, bitmapW, bitmapH, scale, ascent, descent, lineGap);

        } catch (Exception e) {
            System.err.println("Error creating font: " + e.getMessage());
            e.printStackTrace();

            // Clean up resources on failure
            if (charData != null) {
                charData.free();
            }
            if (textureId != 0) {
                glDeleteTextures(textureId);
            }

            return createFallbackFont(fontSize);
        }
    }

    // Create a simple fallback font when loading fails
    private static GlFontInfo createFallbackFont(int fontSize) {
        System.out.println("Creating fallback font");

        int bitmapW = 128;
        int bitmapH = 128;
        ByteBuffer bitmap = BufferUtils.createByteBuffer(bitmapW * bitmapH);

        // Fill with a basic pattern - simple box for each character
        for (int y = 0; y < bitmapH; y++) {
            for (int x = 0; x < bitmapW; x++) {
                // Create border outline
                boolean isBorder = x < 2 || y < 2 || x >= bitmapW - 2 || y >= bitmapH - 2;
                bitmap.put(y * bitmapW + x, isBorder ? (byte) 0xFF : 0);
            }
        }

        // Create texture
        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, bitmapW, bitmapH, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glBindTexture(GL_TEXTURE_2D, 0);

        // Create simple charData
        STBTTPackedchar.Buffer charData = STBTTPackedchar.malloc(96);

        // Initialize with simple glyph data
        for (int i = 0; i < 96; i++) {
            charData.get(i)
                    .xoff(0).yoff(0)
                    .xoff2(1).yoff2(1)
                    .x0((short)2).y0((short)2)
                    .x1((short)(bitmapW - 2)).y1((short)(bitmapH - 2))
                    .xadvance(fontSize * 0.6f);
        }

        // Use simple metrics
        float scale = fontSize / 16.0f;
        float ascent = 12 * scale;
        float descent = 4 * scale;
        float lineGap = 2 * scale;

        return new GlFontInfo(textureId, charData, bitmapW, bitmapH, scale, ascent, descent, lineGap);
    }

    // Load font data from a resource path
    private static ByteBuffer loadFontData(String fontPath) {
        try (InputStream is = GlFontLoader.class.getResourceAsStream(fontPath)) {
            if (is == null) {
                System.err.println("Font resource not found: " + fontPath);
                return null;
            }

            // Read all bytes
            byte[] data = is.readAllBytes();
            ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
            buffer.put(data);
            buffer.flip();
            return buffer;
        } catch (IOException e) {
            System.err.println("Error loading font: " + e.getMessage());
            return null;
        }
    }
}