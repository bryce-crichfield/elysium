package game.platform.gl;

import game.platform.FrameBuffer;
import game.platform.Renderer;
import game.platform.Transform;
import org.joml.Vector2f;

import java.awt.*;
import java.util.Stack;

import static org.lwjgl.opengl.GL11.*;

public class GlRenderer implements Renderer {
    private Stack<GlTransform> transformStack = new Stack<>();
    private Color color = Color.WHITE;
    private Stroke stroke = new BasicStroke();
    private Composite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);

    public GlRenderer(int screenWidth, int screenHeight) {
        // Push the screen spcae transform onto the stack
        var transform = GlTransform.fromScreenSpace(screenWidth, screenHeight);
        transformStack.push(transform);
    }

    @Override
    public Transform getTransform() {
        return transformStack.isEmpty() ? null : transformStack.peek();
    }

    @Override
    public void setTransform(Transform transform) {

    }

    @Override
    public Shape getClip() {
        return null;
    }

    @Override
    public void setClip(Shape rectangle) {

    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {

    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        // Transform all four corners of the rectangle
        var topLeft = transformStack.peek().transform(new Vector2f(x, y));
        var topRight = transformStack.peek().transform(new Vector2f(x + width, y));
        var bottomRight = transformStack.peek().transform(new Vector2f(x + width, y + height));
        var bottomLeft = transformStack.peek().transform(new Vector2f(x, y + height));

        // Draw using the transformed coordinates directly
        glBegin(GL_QUADS);
        glColor3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
        glVertex2f(topLeft.x, topLeft.y);
        glVertex2f(topRight.x, topRight.y);
        glVertex2f(bottomRight.x, bottomRight.y);
        glVertex2f(bottomLeft.x, bottomLeft.y);
        glEnd();
    }

    @Override
    public void setFont(Font font) {

    }

    @Override
    public void drawString(String str, int x, int y) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public FrameBuffer createFrameBuffer(int screenWidth, int screenHeight) {
        return null;
    }

    @Override
    public Stroke getStroke() {
        return stroke;
    }

    @Override
    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    @Override
    public void drawLine(int startX, int startY, int endX, int endY) {

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
    public void fillOval(int x1, int y1, int x2, int y2) {

    }

    @Override
    public void drawOval(int x1, int y1, int x2, int y2) {

    }

    @Override
    public void translate(float x, float y) {
        var glTransform = new GlTransform();
        glTransform.translate(x, y);
        // compose the current transform with the new one and push it onto the stack
        transformStack.push(glTransform.compose(transformStack.peek()));
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int radius, int radius2) {

    }

    @Override
    public void drawRoundRect(int i, int y, int width, int height, int radius, int radius1) {

    }

    @Override
    public void clip(Rectangle viewportRect) {

    }

    @Override
    public FontMetrics getFontMetrics() {
        return null;
    }

    @Override
    public FontMetrics getFontMetrics(Font font) {
        return null;
    }

    @Override
    public void clipRect(int x, int y, int width, int height) {

    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int i) {

    }

    @Override
    public void drawFrameBuffer(FrameBuffer buffer, int i, int i1) {

    }

    @Override
    public void setPaint(Paint paint) {

    }
}
