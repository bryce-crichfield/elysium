package game.transition;

import game.Game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;

public class PixelateTransition extends Transition {
    private BufferedImage sourceImage;
    private BufferedImage targetImage;
    private int maxPixelSize;
    private boolean isIntro; // true for pixelating in, false for pixelating out

    public PixelateTransition(Duration duration, BufferedImage sourceImage,
                              BufferedImage targetImage, int maxPixelSize, boolean isIntro) {
        super(duration);
        this.sourceImage = sourceImage;
        this.targetImage = targetImage;
        this.maxPixelSize = maxPixelSize;
        this.isIntro = isIntro;
    }

    @Override
    public void render(Graphics2D graphics, int width, int height) {
        float progress = getProgress();

        // Calculate current pixel size based on progress
        int pixelSize;
        if (isIntro) {
            // Start from max pixel size and decrease to 1
            pixelSize = Math.max(1, Math.round(maxPixelSize * (1 - progress)));
        } else {
            // Start from 1 and increase to max pixel size
            pixelSize = Math.max(1, Math.round(maxPixelSize * progress));
        }

        // Create a temporary scaled-down image
        int tempWidth = width / pixelSize;
        int tempHeight = height / pixelSize;

        BufferedImage tempImage = new BufferedImage(tempWidth, tempHeight,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D tempGraphics = tempImage.createGraphics();

        // Choose which image to draw based on progress
        BufferedImage currentImage;
        if (isIntro) {
            currentImage = targetImage;
        } else {
            currentImage = sourceImage;
        }

        // Draw the source image at reduced resolution
        tempGraphics.drawImage(currentImage, 0, 0, tempWidth, tempHeight, null);
        tempGraphics.dispose();

        // Draw the reduced resolution image back at full size with nearest-neighbor scaling
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        graphics.drawImage(tempImage, 0, 0, width, height, null);
    }

    // Method to capture the current screen as a source image
    public static BufferedImage captureScreen(Game game) {
        BufferedImage image = new BufferedImage(Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        // Render the current game state to the image
        if (!game.getStateManager().getStateStack().isEmpty()) {
            game.getStateManager().getStateStack().peek().onRender(g);
        }

        g.dispose();
        return image;
    }
}
