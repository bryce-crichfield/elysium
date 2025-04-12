package game.platform.awt;

import game.platform.FontInfo;
import game.platform.FrameBuffer;
import game.platform.Renderer;
import game.platform.Transform;

import java.awt.*;

public class AwtRenderer implements Renderer {
    private final Graphics2D graphics;

    public AwtRenderer(Graphics2D graphics) {
        this.graphics = graphics;
    }

    @Override
    public Transform getTransform() {
        AwtTransform transform = new AwtTransform(graphics.getTransform());
        return transform;
    }

    @Override
    public void setTransform(Transform transform) {
        if (transform instanceof AwtTransform) {
            AwtTransform awtTransform = (AwtTransform) transform;
            graphics.setTransform(awtTransform.getTransform());
        } else {
            throw new IllegalArgumentException("Transform must be an instance of AwtTransform");
        }
    }

    @Override
    public void pushTransform(Transform transform) {

    }

    @Override
    public Transform popTransform() {
        return null;
    }

    @Override
    public void pushClip(int x, int y, int width, int height) {

    }

    @Override
    public void popClip() {

    }

    @Override
    public void setColor(Color color) {
        graphics.setColor(color);
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
        graphics.drawRect(x, y, width, height);
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        graphics.fillRect(x, y, width, height);
    }

    @Override
    public void setFont(Font font) {
        graphics.setFont(font);
    }

    @Override
    public void drawString(String str, int x, int y) {
        graphics.drawString(str, x, y);
    }

    @Override
    public void dispose() {
        graphics.dispose();
    }

    @Override
    public FrameBuffer createFrameBuffer(int screenWidth, int screenHeight) {
        return new AwtFrameBuffer(screenWidth, screenHeight);
    }

    @Override
    public int getLineWidth() {
        if (graphics.getStroke() instanceof BasicStroke) {
            return (int) ((BasicStroke) graphics.getStroke()).getLineWidth();
        }
        return 1; // Default line width
    }

    @Override
    public void setLineWidth(int width) {
        var stroke = new BasicStroke(width);
        graphics.setStroke(stroke);
    }

    @Override
    public void drawLine(int startX, int startY, int endX, int endY) {
        graphics.drawLine(startX, startY, endX, endY);
    }

    @Override
    public Composite getComposite() {
        return graphics.getComposite();
    }

    @Override
    public void setComposite(Composite instance) {
        graphics.setComposite(instance);
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        graphics.fillOval(x, y, width, height);
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        graphics.drawOval(x, y, width, height);
    }

    @Override
    public void translate(float x, float y) {
        graphics.translate(x, y);
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        graphics.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        graphics.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public FontInfo getFontMetrics() {
        return new AwtFontInfo(graphics.getFontMetrics());
    }

    @Override
    public FontInfo getFontMetrics(Font font) {
        graphics.setFont(font);
        return new AwtFontInfo(graphics.getFontMetrics());
    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        graphics.fillPolygon(xPoints, yPoints, nPoints);
    }

    @Override
    public void drawFrameBuffer(FrameBuffer buffer, int x, int y) {
        // Implementation depends on how AwtFrameBuffer is structured
        // Assuming we need to access the internal BufferedImage from AwtFrameBuffer
        if (buffer instanceof AwtFrameBuffer) {
            // This would require adding a getter for the image in AwtFrameBuffer
             graphics.drawImage(((AwtFrameBuffer) buffer).getImage(), x, y, null);

            // Since we don't have direct access to the BufferedImage, we'll need a workaround
            // This is a placeholder implementation
        }
    }

    @Override
    public void setPaint(Paint gradient) {
        graphics.setPaint(gradient);
    }
}