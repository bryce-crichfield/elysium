package game.transition;

import game.platform.Renderer;

import java.awt.image.BufferedImage;
import java.time.Duration;

public class WipeTransition extends Transition {
    private final BufferedImage sourceImage;
    private final BufferedImage targetImage;
    private final Direction direction;
    public WipeTransition(Duration duration, BufferedImage sourceImage,
                          BufferedImage targetImage, Direction direction) {
        super(duration);
        this.sourceImage = sourceImage;
        this.targetImage = targetImage;
        this.direction = direction;
    }

    @Override
    public void render(Renderer graphics, int width, int height) {
//        float progress = getProgress();
//
//        // Draw the source image as background
//        if (sourceImage != null) {
//            graphics.drawImage(sourceImage, 0, 0, null);
//        }
//
//        // Calculate wipe position based on direction
//        int wipePosition;
//        Rectangle clipRect = new Rectangle(0, 0, width, height);
//
//        switch (direction) {
//            case LEFT_TO_RIGHT:
//                wipePosition = (int) (width * progress);
//                clipRect.x = 0;
//                clipRect.width = wipePosition;
//                break;
//            case RIGHT_TO_LEFT:
//                wipePosition = (int) (width * (1 - progress));
//                clipRect.x = wipePosition;
//                clipRect.width = width - wipePosition;
//                break;
//            case TOP_TO_BOTTOM:
//                wipePosition = (int) (height * progress);
//                clipRect.y = 0;
//                clipRect.height = wipePosition;
//                break;
//            case BOTTOM_TO_TOP:
//                wipePosition = (int) (height * (1 - progress));
//                clipRect.y = wipePosition;
//                clipRect.height = height - wipePosition;
//                break;
//            default:
//                return;
//        }
//
//        // Set clip and draw target image
//        Shape oldClip = graphics.getClip();
//        graphics.setClip(clipRect);
//
//        if (targetImage != null) {
//            graphics.drawImage(targetImage, 0, 0, null);
//        }
//
//        // Restore original clip
//        graphics.setClip(oldClip);
//
//        // Draw a line at the wipe edge for a cleaner look
//        graphics.setColor(Color.WHITE);
//        switch (direction) {
//            case LEFT_TO_RIGHT:
//                graphics.drawLine(wipePosition, 0, wipePosition, height);
//                break;
//            case RIGHT_TO_LEFT:
//                graphics.drawLine(wipePosition, 0, wipePosition, height);
//                break;
//            case TOP_TO_BOTTOM:
//                graphics.drawLine(0, wipePosition, width, wipePosition);
//                break;
//            case BOTTOM_TO_TOP:
//                graphics.drawLine(0, wipePosition, width, wipePosition);
//                break;
//        }
    }

    public enum Direction {
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT,
        TOP_TO_BOTTOM,
        BOTTOM_TO_TOP
    }
}