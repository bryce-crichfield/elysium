package game.transition;

import game.platform.FrameBuffer;
import game.platform.Renderer;
import game.util.Easing;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;

public class FadeTransition extends Transition {
    private Color color;
    private final Easing easing;
    private final FrameBuffer sourceImage;
    private final FrameBuffer targetImage;

    public FadeTransition(FrameBuffer sourceImage, FrameBuffer targetImage,
                          Duration duration, Color color, Easing easing) {
        super(duration);
        this.sourceImage = sourceImage;
        this.targetImage = targetImage;
        this.color = color;
        this.easing = easing;
    }

    @Override
    public void render(Renderer graphics, int width, int height) {
        float progress = getProgress() * duration.toMillis();
        float alpha = easing.ease(0, 1, duration.toMillis(), progress);

        // First phase (0 to 0.5): Fade out source image
        if (alpha < 0.5f) {
            // Set the color to (alpha * 2)
            color = new Color(color.getRed(), color.getGreen(), color.getBlue(),
                    (int) (255 * (alpha * 2)));
            graphics.drawFrameBuffer(sourceImage, 0, 0);
            graphics.setColor(color);
            graphics.fillRect(0, 0, width, height);
        }
        // Second phase (0.5 to 1.0): Fade in target image
        else {
            // Set the color to 2 - (alpha * 2)
            color = new Color(color.getRed(), color.getGreen(), color.getBlue(),
                    (int) (255 * (2 - (alpha * 2))));
            graphics.drawFrameBuffer(targetImage, 0, 0);
            graphics.setColor(color);
            graphics.fillRect(0, 0, width, height);
        }
    }
}