package client.core.transition;

import client.core.graphics.FrameBuffer;
import client.core.graphics.Renderer;

import java.time.Duration;

public class PixelateTransition extends Transition {
    private final FrameBuffer sourceImage;
    private final FrameBuffer targetImage;
    private final int maxPixelSize;
    private final boolean isIntro; // true for pixelating in, false for pixelating out

    public PixelateTransition(Duration duration, FrameBuffer sourceImage,
                              FrameBuffer targetImage, int maxPixelSize, boolean isIntro) {
        super(duration);
        this.sourceImage = sourceImage;
        this.targetImage = targetImage;
        this.maxPixelSize = maxPixelSize;
        this.isIntro = isIntro;
    }

    @Override
    public void render(Renderer renderer, int width, int height) {

    }
}