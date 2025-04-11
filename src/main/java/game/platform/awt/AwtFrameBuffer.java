package game.platform.awt;

import game.platform.FrameBuffer;
import game.platform.Renderer;

import java.awt.*;
import java.awt.image.BufferedImage;

public class AwtFrameBuffer implements FrameBuffer {
    private final BufferedImage image;

    public AwtFrameBuffer(int screenWidth, int screenHeight) {
        this.image = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    public Renderer createRenderer() {
        return new AwtRenderer(image.createGraphics());
    }

    public Image getImage() {
        return image;
    }
}
