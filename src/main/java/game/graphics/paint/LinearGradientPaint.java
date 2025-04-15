package game.graphics.paint;

import game.graphics.Renderer;

import java.awt.*;

public class LinearGradientPaint implements Paint {
    private final float startX, startY;
    private final float endX, endY;
    private final Color color1, color2;

    public LinearGradientPaint(float startX, float startY, float endX, float endY,
                               Color color1, Color color2) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.color1 = color1;
        this.color2 = color2;
    }

    @Override
    public void apply(Renderer renderer, int x, int y, int width, int height) {
        // This will be handled differently - see fillRect implementation
    }

    public float getStartX() { return startX; }
    public float getStartY() { return startY; }
    public float getEndX() { return endX; }
    public float getEndY() { return endY; }
    public Color getColor1() { return color1; }
    public Color getColor2() { return color2; }
}
