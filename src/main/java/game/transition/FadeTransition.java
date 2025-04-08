package game.transition;

import game.util.Easing;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;

public class FadeTransition extends Transition {
    private final Color color;
    private final Easing easing;
    private final BufferedImage sourceImage;
    private final BufferedImage targetImage;

    public FadeTransition(BufferedImage sourceImage, BufferedImage targetImage,
                          Duration duration, Color color, Easing easing) {
        super(duration);
        this.sourceImage = sourceImage;
        this.targetImage = targetImage;
        this.color = color;
        this.easing = easing;
    }

    @Override
    public void render(Graphics2D graphics, int width, int height) {
        float progress = getProgress() * duration.toMillis();
        float alpha = easing.ease(0, 1, duration.toMillis(), progress);

        // First phase (0 to 0.5): Fade out source image
        if (alpha < 0.5f) {
            // Draw source image
            graphics.drawImage(sourceImage, 0, 0, null);

            // Draw overlay with increasing opacity
            Composite originalComposite = graphics.getComposite();
            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * 2)); // Scale to 0-1
            graphics.setColor(color);
            graphics.fillRect(0, 0, width, height);
            graphics.setComposite(originalComposite);
        }
        // Second phase (0.5 to 1.0): Fade in target image
        else {
            // Draw target image
            graphics.drawImage(targetImage, 0, 0, null);

            // Draw overlay with decreasing opacity
            Composite originalComposite = graphics.getComposite();
            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 2 - (alpha * 2))); // Scale from 1-0
            graphics.setColor(color);
            graphics.fillRect(0, 0, width, height);
            graphics.setComposite(originalComposite);
        }
    }
}