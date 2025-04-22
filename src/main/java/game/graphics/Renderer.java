package game.graphics;

import game.graphics.font.FontInfo;
import game.graphics.font.FontRenderer;
import game.graphics.paint.Paint;
import game.graphics.paint.SolidColorPaint;
import game.gui.style.GuiFont;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.awt.*;
import java.util.Stack;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class Renderer {
    private final Stack<Transform> transformStack = new Stack<>();
    private final int bufferWidth;
    private final int bufferHeight;
    private final FontRenderer fontRenderer;
    private final FrameBuffer parentFramebuffer;
    private final Stack<Rectangle> clipStack = new Stack<>();
    private final FontRenderer renderer = new FontRenderer();
    private final int fboId;
    @Getter
    private Color color;
    @Setter
    @Getter
    private int lineWidth = 1;
    @Setter
    @Getter
    private Composite composite;
    @Getter
    private game.graphics.paint.Paint paint;

    public Renderer(FrameBuffer frameBuffer) {
        this.parentFramebuffer = frameBuffer;

        // Set the viewport to the screen dimensions
        this.fboId = frameBuffer.getFboId();
        this.bufferWidth = frameBuffer.getWidth();
        this.bufferHeight = frameBuffer.getHeight();

        // Clear the apply stack before pushing a new one
        transformStack.clear();

        // Initialize default state
        color = Color.WHITE;
        composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);

        // Apply default state to OpenGL
        glLineWidth(lineWidth);
        applyColor(color);

        // Initialize the font renderer
        fontRenderer = new FontRenderer();

        // Enable blending for transparency
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public void pushTransform(Transform transform) {
        if (transformStack.isEmpty()) {
            // If the stack is empty, just push the new apply
            transformStack.push(transform);
        } else {
            // Compose the current apply with the new one and push it onto the stack
            Transform current = transformStack.peek().copy();
            transformStack.push(transform.compose(current));
        }
    }

    public void popTransform() {
        if (!transformStack.isEmpty()) {
            transformStack.pop();
        }
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
            Transform transform = transformStack.peek();

            // Create transformed versions of both rectangles
            Rectangle transformedCurrent = transformRectangle(currentClip, transform);
            Rectangle transformedNew = transformRectangle(newClip, transform);

            // Calculate intersection in transformed space
            Rectangle transformedIntersection = transformedCurrent.intersection(transformedNew);

            // Convert back to untransformed space for storage
            Transform inverseTransform = transform.inverse();

            // Convert intersection back to untransformed coordinates for storage
            Rectangle untransformedIntersection = transformRectangle(transformedIntersection, inverseTransform);
            clipStack.push(untransformedIntersection);
        }

        applyCurrentClip(clipStack.peek());
    }

    private Rectangle transformRectangle(Rectangle rect, Transform transform) {
        // Transform all four corners and find the bounding box
        Vector2f topLeft = new Vector2f(rect.x, rect.y);
        Vector2f topRight = new Vector2f(rect.x + rect.width, rect.y);
        Vector2f bottomLeft = new Vector2f(rect.x, rect.y + rect.height);
        Vector2f bottomRight = new Vector2f(rect.x + rect.width, rect.y + rect.height);

        topLeft = transform.apply(topLeft);
        topRight = transform.apply(topRight);
        bottomLeft = transform.apply(bottomLeft);
        bottomRight = transform.apply(bottomRight);

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
        setColor(currentColor);

        // Apply the current transform to the clip rectangle
        Vector2f clipTopLeft = transformStack.peek().apply(new Vector2f(clip.x, clip.y));
        Vector2f clipBottomRight = transformStack.peek().apply(new Vector2f(clip.x + clip.width, clip.y + clip.height));

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

    public void setColor(Color color) {
        this.color = color;
        applyColor(color);
    }

    public void setPaint(Paint paint) {
        this.paint = paint;

        // If it's a solid color paint, update the color
        if (paint instanceof SolidColorPaint) {
            this.color = ((SolidColorPaint) paint).getColor();
            applyColor(this.color);
        }
    }

    private Color interpolateColor(Color c1, Color c2, float ratio) {
        int r = (int) (c1.getRed() + ratio * (c2.getRed() - c1.getRed()));
        int g = (int) (c1.getGreen() + ratio * (c2.getGreen() - c1.getGreen()));
        int b = (int) (c1.getBlue() + ratio * (c2.getBlue() - c1.getBlue()));
        int a = (int) (c1.getAlpha() + ratio * (c2.getAlpha() - c1.getAlpha()));

        return new Color(r, g, b, a);
    }

    public void drawRect(int x, int y, int width, int height) {
        ensureCorrectFboBound();

        // Temporarily push a new apply to the stack (fromScreenSpace) to take our screen space coordinates
        // and convert them to NDC space
        pushTransform(Transform.fromScreenSpace(bufferWidth, bufferHeight));

        var topLeft = transformStack.peek().apply(new Vector2f(x, y));
        var topRight = transformStack.peek().apply(new Vector2f(x + width, y));
        var bottomRight = transformStack.peek().apply(new Vector2f(x + width, y + height));
        var bottomLeft = transformStack.peek().apply(new Vector2f(x, y + height));
        applyColor(color);

        glLineWidth(lineWidth);

        glBegin(GL_LINE_LOOP);
        glVertex2f(topLeft.x, topLeft.y);
        glVertex2f(topRight.x, topRight.y);
        glVertex2f(bottomRight.x, bottomRight.y);
        glVertex2f(bottomLeft.x, bottomLeft.y);
        glEnd();

        // Pop the apply stack to restore the previous state
        popTransform();
    }

    // Update the fillRect method to handle gradients
    public void fillRect(int x, int y, int width, int height) {
        ensureCorrectFboBound();

        // Check if we're using a LinearGradientPaint
        if (paint instanceof game.graphics.paint.LinearGradientPaint gradient) {
            fillRectWithGradient(x, y, width, height, gradient);
            return;
        }

        // Original solid color implementation
        pushTransform(Transform.fromScreenSpace(bufferWidth, bufferHeight));

        // Transform all four corners of the rectangle
        var topLeft = transformStack.peek().apply(new Vector2f(x, y));
        var topRight = transformStack.peek().apply(new Vector2f(x + width, y));
        var bottomRight = transformStack.peek().apply(new Vector2f(x + width, y + height));
        var bottomLeft = transformStack.peek().apply(new Vector2f(x, y + height));

        applyColor(color);

        // Draw using the transformed coordinates directly
        glBegin(GL_QUADS);
        glVertex2f(topLeft.x, topLeft.y);
        glVertex2f(topRight.x, topRight.y);
        glVertex2f(bottomRight.x, bottomRight.y);
        glVertex2f(bottomLeft.x, bottomLeft.y);
        glEnd();

        // Pop the apply stack to restore the previous state
        popTransform();
    }

    // New method to fill rectangle with gradient
    // Updated method to fill rectangle with gradient in any direction
    private void fillRectWithGradient(int x, int y, int width, int height, game.graphics.paint.LinearGradientPaint gradient) {
        pushTransform(Transform.fromScreenSpace(bufferWidth, bufferHeight));

        Transform transform = transformStack.peek();

        // Calculate gradient vector direction
        float gradientDirX = gradient.getEndX() - gradient.getStartX();
        float gradientDirY = gradient.getEndY() - gradient.getStartY();
        float gradientLength = (float) Math.sqrt(gradientDirX * gradientDirX + gradientDirY * gradientDirY);

        // Normalize direction vector
        if (gradientLength > 0) {
            gradientDirX /= gradientLength;
            gradientDirY /= gradientLength;
        }

        // We'll divide the rectangle into multiple strips for the gradient
        int segments = 20; // More segments = smoother gradient

        // For top-to-bottom gradient, we need to divide along the Y axis
        if (Math.abs(gradientDirY) > Math.abs(gradientDirX)) {
            // Vertical gradient (top to bottom or bottom to top)
            glBegin(GL_QUADS);

            for (int i = 0; i < segments; i++) {
                float t1 = (float) i / segments;
                float t2 = (float) (i + 1) / segments;

                // Interpolate between start and end colors
                Color color1 = interpolateColor(gradient.getColor1(), gradient.getColor2(), t1);
                Color color2 = interpolateColor(gradient.getColor1(), gradient.getColor2(), t2);

                // Calculate corner positions for this segment
                float y1 = y + t1 * height;
                float y2 = y + t2 * height;

                Vector2f topLeft = transform.apply(new Vector2f(x, y1));
                Vector2f topRight = transform.apply(new Vector2f(x + width, y1));
                Vector2f bottomRight = transform.apply(new Vector2f(x + width, y2));
                Vector2f bottomLeft = transform.apply(new Vector2f(x, y2));

                // Apply start color
                glColor4f(color1.getRed() / 255f, color1.getGreen() / 255f,
                        color1.getBlue() / 255f, color1.getAlpha() / 255f);
                glVertex2f(topLeft.x, topLeft.y);
                glVertex2f(topRight.x, topRight.y);

                // Apply end color
                glColor4f(color2.getRed() / 255f, color2.getGreen() / 255f,
                        color2.getBlue() / 255f, color2.getAlpha() / 255f);
                glVertex2f(bottomRight.x, bottomRight.y);
                glVertex2f(bottomLeft.x, bottomLeft.y);
            }
        } else {
            // Horizontal gradient (left to right or right to left)
            glBegin(GL_QUADS);

            for (int i = 0; i < segments; i++) {
                float t1 = (float) i / segments;
                float t2 = (float) (i + 1) / segments;

                // Interpolate between start and end colors
                Color color1 = interpolateColor(gradient.getColor1(), gradient.getColor2(), t1);
                Color color2 = interpolateColor(gradient.getColor1(), gradient.getColor2(), t2);

                // Calculate corner positions for this segment
                float x1 = x + t1 * width;
                float x2 = x + t2 * width;

                Vector2f topLeft = transform.apply(new Vector2f(x1, y));
                Vector2f topRight = transform.apply(new Vector2f(x2, y));
                Vector2f bottomRight = transform.apply(new Vector2f(x2, y + height));
                Vector2f bottomLeft = transform.apply(new Vector2f(x1, y + height));

                // Apply start color
                glColor4f(color1.getRed() / 255f, color1.getGreen() / 255f,
                        color1.getBlue() / 255f, color1.getAlpha() / 255f);
                glVertex2f(topLeft.x, topLeft.y);
                glVertex2f(bottomLeft.x, bottomLeft.y);

                // Apply end color
                glColor4f(color2.getRed() / 255f, color2.getGreen() / 255f,
                        color2.getBlue() / 255f, color2.getAlpha() / 255f);
                glVertex2f(bottomRight.x, bottomRight.y);
                glVertex2f(topRight.x, topRight.y);
            }
        }

        glEnd();
        popTransform();
    }

    public void drawLine(int startX, int startY, int endX, int endY) {
        ensureCorrectFboBound();

        pushTransform(Transform.fromScreenSpace(bufferWidth, bufferHeight));
        var start = transformStack.peek().apply(new Vector2f(startX, startY));
        var end = transformStack.peek().apply(new Vector2f(endX, endY));

        glLineWidth(lineWidth);
        applyColor(color);

        glBegin(GL_LINES);
        glVertex2f(start.x, start.y);
        glVertex2f(end.x, end.y);
        glEnd();

        popTransform();
    }

    public void fillOval(int x, int y, int width, int height) {
        ensureCorrectFboBound();

        pushTransform(Transform.fromScreenSpace(bufferWidth, bufferHeight));
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
        Vector2f center = transformStack.peek().apply(new Vector2f(centerX, centerY));
        glVertex2f(center.x, center.y);

        // Create vertices around the oval
        for (int i = 0; i <= segments; i++) {
            float angle = (float) (2.0 * Math.PI * i / segments);
            float pointX = centerX + radiusX * (float) Math.cos(angle);
            float pointY = centerY + radiusY * (float) Math.sin(angle);

            // Transform the point according to the current apply stack
            Vector2f point = transformStack.peek().apply(new Vector2f(pointX, pointY));
            glVertex2f(point.x, point.y);
        }

        glEnd();

        // Pop the apply stack to restore the previous state
        popTransform();
    }

    public void drawOval(int x, int y, int width, int height) {
        ensureCorrectFboBound();

        pushTransform(Transform.fromScreenSpace(bufferWidth, bufferHeight));
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

            // Transform the point according to the current apply stack
            Vector2f point = transformStack.peek().apply(new Vector2f(pointX, pointY));
            glVertex2f(point.x, point.y);
        }

        glEnd();

        // Pop the apply stack to restore the previous state
        popTransform();
    }

    public void fillRoundRect(int x, int y, int width, int height, int radiusX, int radiusY) {
        ensureCorrectFboBound();
        pushTransform(Transform.fromScreenSpace(bufferWidth, bufferHeight));
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

        // Pop the apply stack to restore the previous state
        popTransform();
    }

    private void drawCorner(Transform transform, float cx, float cy, float rx, float ry, int startAngleDeg, int endAngleDeg) {
        glBegin(GL_TRIANGLE_FAN);
        Vector2f center = transform.apply(new Vector2f(cx, cy));
        glVertex2f(center.x, center.y);

        for (int angle = startAngleDeg; angle <= endAngleDeg; angle += 5) {
            float rad = (float) Math.toRadians(angle);
            float x = cx + (float) Math.cos(rad) * rx;
            float y = cy + (float) Math.sin(rad) * ry;
            Vector2f pt = transform.apply(new Vector2f(x, y));
            glVertex2f(pt.x, pt.y);
        }

        glEnd();
    }

    public void drawRoundRect(int x, int y, int width, int height, int radiusX, int radiusY) {
        ensureCorrectFboBound();
        pushTransform(Transform.fromScreenSpace(bufferWidth, bufferHeight));
        var transform = transformStack.peek();
        applyColor(color);
        glLineWidth(lineWidth);

        glBegin(GL_LINE_STRIP);
        drawArc(transform, x + radiusX, y + radiusY, radiusX, radiusY, 180, 270); // top-left
        glVertex2f(transform.apply(new Vector2f(x + radiusX, y)).x, transform.apply(new Vector2f(x + radiusX, y)).y);
        glVertex2f(transform.apply(new Vector2f(x + width - radiusX, y)).x, transform.apply(new Vector2f(x + width - radiusX, y)).y);
        drawArc(transform, x + width - radiusX, y + radiusY, radiusX, radiusY, 270, 360); // top-right
        glVertex2f(transform.apply(new Vector2f(x + width, y + radiusY)).x, transform.apply(new Vector2f(x + width, y + radiusY)).y);
        glVertex2f(transform.apply(new Vector2f(x + width, y + height - radiusY)).x, transform.apply(new Vector2f(x + width, y + height - radiusY)).y);
        drawArc(transform, x + width - radiusX, y + height - radiusY, radiusX, radiusY, 0, 90); // bottom-right
        glVertex2f(transform.apply(new Vector2f(x + width - radiusX, y + height)).x, transform.apply(new Vector2f(x + width - radiusX, y + height)).y);
        glVertex2f(transform.apply(new Vector2f(x + radiusX, y + height)).x, transform.apply(new Vector2f(x + radiusX, y + height)).y);
        drawArc(transform, x + radiusX, y + height - radiusY, radiusX, radiusY, 90, 180); // bottom-left
        glVertex2f(transform.apply(new Vector2f(x, y + height - radiusY)).x, transform.apply(new Vector2f(x, y + height - radiusY)).y);
        glVertex2f(transform.apply(new Vector2f(x, y + radiusY)).x, transform.apply(new Vector2f(x, y + radiusY)).y);
        glEnd();

        popTransform();
    }

    private void drawArc(Transform transform, float cx, float cy, float rx, float ry, int startAngleDeg, int endAngleDeg) {
        for (int angle = startAngleDeg; angle <= endAngleDeg; angle += 5) {
            float rad = (float) Math.toRadians(angle);
            float x = cx + (float) Math.cos(rad) * rx;
            float y = cy + (float) Math.sin(rad) * ry;
            Vector2f pt = transform.apply(new Vector2f(x, y));
            glVertex2f(pt.x, pt.y);
        }
    }

    public void fillPolygon(int[] xPoints, int[] yPoints, int i) {
        pushTransform(Transform.fromScreenSpace(bufferWidth, bufferHeight));
        glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        glBegin(GL_POLYGON);
        for (int j = 0; j < i; j++) {
            var point = transformStack.peek().apply(new Vector2f(xPoints[j], yPoints[j]));
            glVertex2f(point.x, point.y);
        }
        glEnd();
        popTransform();
    }

    // Helper method to ensure the current FBO is bound before drawing operations
    private void ensureCorrectFboBound() {
        int[] currentFbo = new int[1];
        GL30.glGetIntegerv(GL30.GL_FRAMEBUFFER_BINDING, currentFbo);
        if (currentFbo[0] != fboId) {
            glBindFramebuffer(GL_FRAMEBUFFER, fboId);

            // Update bound state of parent framebuffer if we have one
            if (parentFramebuffer != null) {
                parentFramebuffer.setBound(true);
            }
        }
    }

    public void drawFrameBuffer(FrameBuffer buffer, int x, int y, int width, int height) {
        // Ensure we're drawing to our framebuffer
        ensureCorrectFboBound();

        pushTransform(Transform.fromScreenSpace(bufferWidth, bufferHeight));

        int textureId = buffer.getTextureId();
        int fbWidth = buffer.getWidth();
        int fbHeight = buffer.getHeight();

        // Save texture state
        int[] currentTexture = new int[1];
        glGetIntegerv(GL_TEXTURE_BINDING_2D, currentTexture);

        // Enable texturing
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Apply a white color to ensure the texture is drawn correctly
        glColor4f(1, 1, 1, 1);

        // Transform the coordinates - now using width and height parameters instead of fbWidth and fbHeight
        Vector2f topLeft = transformStack.peek().apply(new Vector2f(x, y));
        Vector2f topRight = transformStack.peek().apply(new Vector2f(x + width, y));
        Vector2f bottomRight = transformStack.peek().apply(new Vector2f(x + width, y + height));
        Vector2f bottomLeft = transformStack.peek().apply(new Vector2f(x, y + height));

        // Draw the quad but inverted so it's flipped over the y-axis (I'm not sure why we need to do this, but it works)
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

    public void drawFrameBuffer(FrameBuffer buffer, int i, int i1) {
        drawFrameBuffer(buffer, i, i1, buffer.getWidth(), buffer.getHeight());
    }

    public void setFont(String name, int size) {
        fontRenderer.setFont(name, size);
    }

    public void drawString(String str, int x, int y) {
        // Ensure we're drawing to our framebuffer
        ensureCorrectFboBound();

        // Calculate the transformed position
//        System.out.println("Drawing string: " + str + " at (" + x + ", " + y + ")");
        Vector2f originalPos = new Vector2f(x, y);
        Vector2f transformedPos = originalPos;

        // Apply the apply stack if it's not empty
        if (!transformStack.isEmpty()) {
            transformedPos = transformStack.peek().apply(originalPos);
        }

        // Setup direct ortho projection for text rendering
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0, bufferWidth, bufferHeight, 0, -1, 1);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();

        // Draw text at the transformed position
        fontRenderer.drawString(str, (int) transformedPos.x, (int) transformedPos.y, color);

        // Restore matrices
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
    }

    public FontInfo getFontInfo() {
        return fontRenderer.getFontInfo();
    }

    public FontInfo getFontInfo(String name, int size) {
        return fontRenderer.getFontInfo(name, size);
    }

    public void dispose() {

    }
}
