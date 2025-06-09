package client.core.graphics.paint;

import client.core.graphics.Renderer;

import java.awt.*;

public class SolidColorPaint implements Paint {
    private final Color color;

    public SolidColorPaint(Color color) {
        this.color = color;
    }

    @Override
    public void apply(Renderer renderer, int x, int y, int width, int height) {
        renderer.setColor(color);
    }

    public Color getColor() {
        return color;
    }
}
