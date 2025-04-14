package game.platform.gl;

import game.platform.FontInfo;
import game.platform.FrameBuffer;
import game.platform.Renderer;
import game.platform.Transform;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.awt.*;
import java.util.Stack;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class GlRenderer implements Renderer {
    private final Stack<GlTransform> transformStack = new Stack<>();
    private final int bufferWidth;
    private final int bufferHeight;
    private Color color = Color.WHITE;
    private int lineWidth = 1;
    private Composite composite;
    private final GlFontRenderer fontRenderer;
    private Font currentFont;

    private final GlFrameBuffer parentFramebuffer;
    private int fboId;
    // Add to your GlRenderer class
    private final Stack<Rectangle> clipStack = new Stack<>();

    public GlRenderer(GlFrameBuffer frameBuffer) {
        // Ensure we're bound to the correct FBO before initializing
        this.parentFramebuffer = frameBuffer;
//        glBindFramebuffer(GL_FRAMEBUFFER, fboId);


        // Set the viewport to the screen dimensions
        this.fboId = frameBuffer.getFboId();
        this.bufferWidth = frameBuffer.getWidth();
        this.bufferHeight = frameBuffer.getHeight();
//        glViewport(0, 0, bufferWidth, screenHeight);

        // Clear the transform stack before pushing a new one
        transformStack.clear();

        // Initialize default state
        color = Color.WHITE;
        lineWidth = 1;
        composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);

        // Apply default state to OpenGL
        glLineWidth(lineWidth);
        applyColor(color);

        // Initialize the font renderer
        fontRenderer = new GlFontRenderer();
        currentFont = new Font("/fonts/arial", Font.PLAIN, 12);

        // Enable blending for transparency
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public Transform getTransform() {
        return transformStack.isEmpty() ? null : transformStack.peek().copy();
    }

    @Override
    public void setTransform(Transform transform) {

    }

    public void pushTransform(Transform transform) {
        if (transform instanceof GlTransform glTransform) {
            if (transformStack.isEmpty()) {
                // If the stack is empty, just push the new transform
                transformStack.push(glTransform);
            } else {
                // Compose the current transform with the new one and push it onto the stack
                GlTransform current = (GlTransform) transformStack.peek().copy();
                transformStack.push(glTransform.compose(current));
            }
        }

    }

    public Transform popTransform() {
        if (!transformStack.isEmpty()) {
            return transformStack.pop();
        }

        return null;
    }

    @Override
    public void clearTransform() {
        transformStack.clear();
    }

    @Override
    public void clearClip() {
        clipStack.clear();
    }

    public void pushClip(int x, int y, int width, int height) {
        // Store clips in their original untransformed coordinates
        Rectangle newClip = new Rectangle(x, y, width, height);

        if (clipStack.isEmpty()) {
            clipStack.push(newClip);
        } else {
            // Get the current clip in untransformed coordinates
            Rectangle currentClip = clipStack.peek();

            // Apply the current transformation to both rectangles to get them in the same space
            GlTransform transform = transformStack.peek();

            // Create transformed versions of both rectangles
            Rectangle transformedCurrent = transformRectangle(currentClip, transform);
            Rectangle transformedNew = transformRectangle(newClip, transform);

            // Calculate intersection in transformed space
            Rectangle transformedIntersection = transformedCurrent.intersection(transformedNew);

            // Convert back to untransformed space for storage
            GlTransform inverseTransform = (GlTransform) transform.inverse();

            // Convert intersection back to untransformed coordinates for storage
            Rectangle untransformedIntersection = transformRectangle(transformedIntersection, inverseTransform);
            clipStack.push(untransformedIntersection);
        }

        applyCurrentClip(clipStack.peek());
    }

    private Rectangle transformRectangle(Rectangle rect, GlTransform transform) {
        // Transform all four corners and find the bounding box
        Vector2f topLeft = new Vector2f(rect.x, rect.y);
        Vector2f topRight = new Vector2f(rect.x + rect.width, rect.y);
        Vector2f bottomLeft = new Vector2f(rect.x, rect.y + rect.height);
        Vector2f bottomRight = new Vector2f(rect.x + rect.width, rect.y + rect.height);

        topLeft = transform.transform(topLeft);
        topRight = transform.transform(topRight);
        bottomLeft = transform.transform(bottomLeft);
        bottomRight = transform.transform(bottomRight);

        // Find bounds of transformed rectangle
        float minX = Math.min(Math.min(topLeft.x, topRight.x), Math.min(bottomLeft.x, bottomRight.x));
        float minY = Math.min(Math.min(topLeft.y, topRight.y), Math.min(bottomLeft.y, bottomRight.y));
        float maxX = Math.max(Math.max(topLeft.x, topRight.x), Math.max(bottomLeft.x, bottomRight.x));
        float maxY = Math.max(Math.max(topLeft.y, topRight.y), Math.max(bottomLeft.y, bottomRight.y));

        return new Rectangle((int) minX, (int) minY, (int) (maxX - minX), (int) (maxY - minY));
    }

    public void popClip() {
        if (!clipStack.isEmpty()) {
            clipStack.pop();
        }

        if (!clipStack.isEmpty()) {
            // If there's still a clip, apply it
            applyCurrentClip(clipStack.peek());
        } else {
            // Otherwise, disable the scissor test
            glDisable(GL_SCISSOR_TEST);
        }
    }


    private void applyCurrentClip(Rectangle clip) {
        var currentColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 100);
        setColor(Color.RED);
        drawRect(clip.x, clip.y, clip.width, clip.height);
        setColor(currentColor);

        // Apply the transform to the clip rectangle
        Vector2f clipTopLeft = transformStack.peek().transform(new Vector2f(clip.x, clip.y));
        Vector2f clipBottomRight = transformStack.peek().transform(new Vector2f(clip.x + clip.width, clip.y + clip.height));

        int clipX = (int) clipTopLeft.x;
        // Flip the Y coordinate for OpenGL (assuming windowHeight is the height of your window)
        int clipY = (int) (bufferHeight - clipBottomRight.y);
        int clipWidth = (int) (clipBottomRight.x - clipTopLeft.x);
        int clipHeight = (int) (clipBottomRight.y - clipTopLeft.y);

        glEnable(GL_SCISSOR_TEST);
        glScissor(clipX, clipY, clipWidth, clipHeight);
    }

    private void applyColor(Color color) {
        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = color.getAlpha() / 255f;
        glColor4f(r, g, b, a);
    }

    @Override
    public void setPaint(Paint paint) {

    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
        applyColor(color);
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
        ensureCorrectFboBound();
        // Temporarily push a new transform to the stack (fromScreenSpace) to take our screen space coordinates
        // and convert them to NDC space
        pushTransform(GlTransform.fromScreenSpace(bufferWidth, bufferHeight));

        var topLeft = transformStack.peek().transform(new Vector2f(x, y));
        var topRight = transformStack.peek().transform(new Vector2f(x + width, y));
        var bottomRight = transformStack.peek().transform(new Vector2f(x + width, y + height));
        var bottomLeft = transformStack.peek().transform(new Vector2f(x, y + height));
        applyColor(color);

        glLineWidth(lineWidth);
        glBegin(GL_LINE_LOOP);
        glVertex2f(topLeft.x, topLeft.y);
        glVertex2f(topRight.x, topRight.y);
        glVertex2f(bottomRight.x, bottomRight.y);
        glVertex2f(bottomLeft.x, bottomLeft.y);
        glEnd();

        // Pop the transform stack to restore the previous state
        popTransform();
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        ensureCorrectFboBound();
        pushTransform(GlTransform.fromScreenSpace(bufferWidth, bufferHeight));

        // Transform all four corners of the rectangle
        var topLeft = transformStack.peek().transform(new Vector2f(x, y));
        var topRight = transformStack.peek().transform(new Vector2f(x + width, y));
        var bottomRight = transformStack.peek().transform(new Vector2f(x + width, y + height));
        var bottomLeft = transformStack.peek().transform(new Vector2f(x, y + height));
        applyColor(color);

        // Draw using the transformed coordinates directly
        glBegin(GL_QUADS);
        glVertex2f(topLeft.x, topLeft.y);
        glVertex2f(topRight.x, topRight.y);
        glVertex2f(bottomRight.x, bottomRight.y);
        glVertex2f(bottomLeft.x, bottomLeft.y);
        glEnd();

        // Pop the transform stack to restore the previous state
        popTransform();
    }


    @Override
    public int getLineWidth() {
        return lineWidth;
    }

    @Override
    public void setLineWidth(int width) {
        lineWidth = width;
    }

    @Override
    public void drawLine(int startX, int startY, int endX, int endY) {
        pushTransform(GlTransform.fromScreenSpace(bufferWidth, bufferHeight));
        var start = transformStack.peek().transform(new Vector2f(startX, startY));
        var end = transformStack.peek().transform(new Vector2f(endX, endY));

        glLineWidth(lineWidth);
        applyColor(color);
        glBegin(GL_LINES);
        glVertex2f(start.x, start.y);
        glVertex2f(end.x, end.y);
        glEnd();
        popTransform();
    }

    @Override
    public Composite getComposite() {
        return composite;
    }

    @Override
    public void setComposite(Composite instance) {
        this.composite = instance;
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        pushTransform(GlTransform.fromScreenSpace(bufferWidth, bufferHeight));
        // Convert from rectangle box (x,y,w,h) to (centerX, centerY, radiusX, radiusY)
        float centerX = x + width / 2.0f;  // Center X is at x plus half the width
        float centerY = y + height / 2.0f; // Center Y is at y plus half the height
        float radiusX = width / 2.0f;      // X radius is half the width
        float radiusY = height / 2.0f;     // Y radius is half the height

        // Number of segments to use for the oval approximation
        int segments = 40;

        // Set the color
        applyColor(color);

        // Draw filled oval using triangle fan
        glBegin(GL_TRIANGLE_FAN);

        // Start with center point
        Vector2f center = transformStack.peek().transform(new Vector2f(centerX, centerY));
        glVertex2f(center.x, center.y);

        // Create vertices around the oval
        for (int i = 0; i <= segments; i++) {
            float angle = (float) (2.0 * Math.PI * i / segments);
            float pointX = centerX + radiusX * (float) Math.cos(angle);
            float pointY = centerY + radiusY * (float) Math.sin(angle);

            // Transform the point according to the current transform stack
            Vector2f point = transformStack.peek().transform(new Vector2f(pointX, pointY));
            glVertex2f(point.x, point.y);
        }

        glEnd();

        // Pop the transform stack to restore the previous state
        popTransform();
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        pushTransform(GlTransform.fromScreenSpace(bufferWidth, bufferHeight));
        // Convert from rectangle box (x,y,w,h) to (centerX, centerY, radiusX, radiusY)
        float centerX = x + width / 2.0f;  // Center X is at x plus half the width
        float centerY = y + height / 2.0f; // Center Y is at y plus half the height
        float radiusX = width / 2.0f;      // X radius is half the width
        float radiusY = height / 2.0f;     // Y radius is half the height

        // Number of segments to use for the oval approximation
        int segments = 40;

        // Set the color
        applyColor(color);

        // Set line width
        glLineWidth(lineWidth);

        // Draw oval outline using line loop
        glBegin(GL_LINE_LOOP);

        // Create vertices around the oval
        for (int i = 0; i < segments; i++) {
            float angle = (float) (2.0 * Math.PI * i / segments);
            float pointX = centerX + radiusX * (float) Math.cos(angle);
            float pointY = centerY + radiusY * (float) Math.sin(angle);

            // Transform the point according to the current transform stack
            Vector2f point = transformStack.peek().transform(new Vector2f(pointX, pointY));
            glVertex2f(point.x, point.y);
        }

        glEnd();

        // Pop the transform stack to restore the previous state
        popTransform();
    }

    @Override
    public void translate(float x, float y) {
        var glTransform = new GlTransform();
        glTransform.translate(x, y);
        // compose the current transform with the new one and push it onto the stack
        transformStack.push(transformStack.peek().compose(glTransform));
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int radiusX, int radiusY) {
        pushTransform(GlTransform.fromScreenSpace(bufferWidth, bufferHeight));
        var transform = transformStack.peek();
        applyColor(color);

        // Fill center rectangle
        fillRect(x + radiusX, y, width - 2 * radiusX, height);
        // Fill left and right rectangles
        fillRect(x, y + radiusY, radiusX, height - 2 * radiusY);
        fillRect(x + width - radiusX, y + radiusY, radiusX, height - 2 * radiusY);

        // Fill corners using triangle fans
        drawCorner(transform, x + radiusX, y + radiusY, radiusX, radiusY, 180, 270); // top-left
        drawCorner(transform, x + width - radiusX, y + radiusY, radiusX, radiusY, 270, 360); // top-right
        drawCorner(transform, x + width - radiusX, y + height - radiusY, radiusX, radiusY, 0, 90); // bottom-right
        drawCorner(transform, x + radiusX, y + height - radiusY, radiusX, radiusY, 90, 180); // bottom-left

        // Pop the transform stack to restore the previous state
        popTransform();
    }

    private void drawCorner(GlTransform transform, float cx, float cy, float rx, float ry, int startAngleDeg, int endAngleDeg) {
        glBegin(GL_TRIANGLE_FAN);
        Vector2f center = transform.transform(new Vector2f(cx, cy));
        glVertex2f(center.x, center.y);

        for (int angle = startAngleDeg; angle <= endAngleDeg; angle += 5) {
            float rad = (float) Math.toRadians(angle);
            float x = cx + (float) Math.cos(rad) * rx;
            float y = cy + (float) Math.sin(rad) * ry;
            Vector2f pt = transform.transform(new Vector2f(x, y));
            glVertex2f(pt.x, pt.y);
        }

        glEnd();
    }


    @Override
    public void drawRoundRect(int x, int y, int width, int height, int radiusX, int radiusY) {
        pushTransform(GlTransform.fromScreenSpace(bufferWidth, bufferHeight));
        var transform = transformStack.peek();
        applyColor(color);
        glLineWidth(lineWidth);

        glBegin(GL_LINE_STRIP);
        drawArc(transform, x + radiusX, y + radiusY, radiusX, radiusY, 180, 270); // top-left
        glVertex2f(transform.transform(new Vector2f(x + radiusX, y)).x, transform.transform(new Vector2f(x + radiusX, y)).y);
        glVertex2f(transform.transform(new Vector2f(x + width - radiusX, y)).x, transform.transform(new Vector2f(x + width - radiusX, y)).y);
        drawArc(transform, x + width - radiusX, y + radiusY, radiusX, radiusY, 270, 360); // top-right
        glVertex2f(transform.transform(new Vector2f(x + width, y + radiusY)).x, transform.transform(new Vector2f(x + width, y + radiusY)).y);
        glVertex2f(transform.transform(new Vector2f(x + width, y + height - radiusY)).x, transform.transform(new Vector2f(x + width, y + height - radiusY)).y);
        drawArc(transform, x + width - radiusX, y + height - radiusY, radiusX, radiusY, 0, 90); // bottom-right
        glVertex2f(transform.transform(new Vector2f(x + width - radiusX, y + height)).x, transform.transform(new Vector2f(x + width - radiusX, y + height)).y);
        glVertex2f(transform.transform(new Vector2f(x + radiusX, y + height)).x, transform.transform(new Vector2f(x + radiusX, y + height)).y);
        drawArc(transform, x + radiusX, y + height - radiusY, radiusX, radiusY, 90, 180); // bottom-left
        glVertex2f(transform.transform(new Vector2f(x, y + height - radiusY)).x, transform.transform(new Vector2f(x, y + height - radiusY)).y);
        glVertex2f(transform.transform(new Vector2f(x, y + radiusY)).x, transform.transform(new Vector2f(x, y + radiusY)).y);
        glEnd();

        popTransform();
    }

    private void drawArc(GlTransform transform, float cx, float cy, float rx, float ry, int startAngleDeg, int endAngleDeg) {
        for (int angle = startAngleDeg; angle <= endAngleDeg; angle += 5) {
            float rad = (float) Math.toRadians(angle);
            float x = cx + (float) Math.cos(rad) * rx;
            float y = cy + (float) Math.sin(rad) * ry;
            Vector2f pt = transform.transform(new Vector2f(x, y));
            glVertex2f(pt.x, pt.y);
        }
    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int i) {
        pushTransform(GlTransform.fromScreenSpace(bufferWidth, bufferHeight));
        glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        glBegin(GL_POLYGON);
        for (int j = 0; j < i; j++) {
            var point = transformStack.peek().transform(new Vector2f(xPoints[j], yPoints[j]));
            glVertex2f(point.x, point.y);
        }
        glEnd();
        popTransform();
    }

    @Override
    public FrameBuffer createFrameBuffer(int width, int height) {
        GlFrameBuffer frameBuffer = new GlFrameBuffer(width, height);
        return frameBuffer;
    }

    // Add helper method to ensure the current FBO is bound before drawing operations
    private void ensureCorrectFboBound() {
        int[] currentFbo = new int[1];
        GL30.glGetIntegerv(GL30.GL_FRAMEBUFFER_BINDING, currentFbo);
        if (currentFbo[0] != fboId) {
            glBindFramebuffer(GL_FRAMEBUFFER, fboId);

            // Update bound state of parent framebuffer if we have one
            if (parentFramebuffer != null) {
                parentFramebuffer.isBound = true;
            }
        }
    }

    // Fix drawFrameBuffer method to properly render the texture
    @Override
    public void drawFrameBuffer(FrameBuffer buffer, int x, int y, int width, int height) {
        if (!(buffer instanceof GlFrameBuffer glFrameBuffer)) {
            return;
        }

        // Ensure we're drawing to our framebuffer
        ensureCorrectFboBound();

        pushTransform(GlTransform.fromScreenSpace(bufferWidth, bufferHeight));

        int textureId = glFrameBuffer.getTextureId();
        int fbWidth = glFrameBuffer.getWidth();
        int fbHeight = glFrameBuffer.getHeight();

        // Save texture state
        int[] currentTexture = new int[1];
        glGetIntegerv(GL_TEXTURE_BINDING_2D, currentTexture);

        // Enable texturing
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Apply a white color to ensure the texture is drawn correctly
        glColor4f(1, 1, 1, 1);

        // Transform the coordinates - now using width and height parameters instead of fbWidth and fbHeight
        Vector2f topLeft = transformStack.peek().transform(new Vector2f(x, y));
        Vector2f topRight = transformStack.peek().transform(new Vector2f(x + width, y));
        Vector2f bottomRight = transformStack.peek().transform(new Vector2f(x + width, y + height));
        Vector2f bottomLeft = transformStack.peek().transform(new Vector2f(x, y + height));

        // Draw the quad but inverted so the its flipped over the y axis (I'm not sure why we need to do this, but it works)
        glBegin(GL_QUADS);
        glTexCoord2f(0, 1);
        glVertex2f(topLeft.x, topLeft.y);
        glTexCoord2f(1, 1);
        glVertex2f(topRight.x, topRight.y);
        glTexCoord2f(1, 0);
        glVertex2f(bottomRight.x, bottomRight.y);
        glTexCoord2f(0, 0);
        glVertex2f(bottomLeft.x, bottomLeft.y);
        glEnd();

        // Restore texture state
        glBindTexture(GL_TEXTURE_2D, currentTexture[0]);
        glDisable(GL_TEXTURE_2D);

        popTransform();
    }

    @Override
    public void drawFrameBuffer(FrameBuffer buffer, int i, int i1) {
        drawFrameBuffer(buffer, i, i1, buffer.getWidth(), buffer.getHeight());
    }

    private final GlFontRenderer renderer = new GlFontRenderer();

    @Override
    public void setFont(Font font) {
        currentFont = font;
        fontRenderer.setFont(font.getName(), font.getSize());
//        fontRenderer.setFont(font);
    }

    @Override
    public void drawString(String str, int x, int y) {
        // Ensure we're drawing to our framebuffer
        ensureCorrectFboBound();

        // Calculate the transformed position
        Vector2f originalPos = new Vector2f(x, y);
        Vector2f transformedPos = originalPos;

        // Apply the transform stack if it's not empty
        if (!transformStack.isEmpty()) {
            transformedPos = transformStack.peek().transform(originalPos);
        }

        // Debug info
        System.out.println("Drawing text: '" + str + "' at original: " + x + ", " + y +
                " transformed: " + transformedPos.x + ", " + transformedPos.y);

        // Setup direct ortho projection for text rendering
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0, bufferWidth, bufferHeight, 0, -1, 1);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();

        // Draw text at the transformed position
        fontRenderer.drawString(str, (int)transformedPos.x, (int)transformedPos.y, color);

        // Restore matrices
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
    }

    @Override
    public FontInfo getFontMetrics() {
        return fontRenderer.getFontInfo();
    }

    @Override
    public FontInfo getFontMetrics(Font font) {
        return getFontMetrics();
//        return fontRenderer.getFontMetrics(font);
    }


    @Override
    public void dispose() {

    }

    public int getFboId() {
        return fboId;
    }

    public void setFboId(int fboId) {
        this.fboId = fboId;
        glBindFramebuffer(GL_FRAMEBUFFER, fboId);
    }
}
