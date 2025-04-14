package game.platform;

import java.awt.*;

public interface Renderer {
    Transform getTransform();

    void setTransform(Transform transform);

    void pushTransform(Transform transform);
    Transform popTransform();
    void clearTransform();

    void pushClip(int x, int y, int width, int height);
    void popClip();
    void clearClip();

    void setColor(Color color);

    void drawRect(int x, int y, int width, int height);

    void fillRect(int x, int y, int width, int height);

    void setFont(Font font);

    void drawString(String str, int x, int y);

    void dispose();

    FrameBuffer createFrameBuffer(int screenWidth, int screenHeight);

    int getLineWidth();
    void setLineWidth(int width);

    void drawLine(int startX, int startY, int endX, int endY);

    Composite getComposite();

    void setComposite(Composite instance);

    void fillOval(int x1, int y1, int x2, int y2);

    void drawOval(int x1, int y1, int x2, int y2);

    void translate(float x, float y);

    void fillRoundRect(int x, int y, int width, int height, int radius, int radius2);

    void drawRoundRect(int i, int y, int width, int height, int radius, int radius1);

    FontInfo getFontMetrics();

    FontInfo getFontMetrics(Font font);

    void fillPolygon(int[] xPoints, int[] yPoints, int i);

    void drawFrameBuffer(FrameBuffer buffer, int i, int i1);
    void drawFrameBuffer(FrameBuffer buffer, int x, int y, int width, int height);

    void setPaint(Paint gradient);

    Color getColor();
}
