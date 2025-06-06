package core.graphics.postprocessing;

import java.awt.image.BufferedImage;
import java.time.Duration;

/**
 * Represents a post-processing effect that can be applied to the game's graphics
 */
public interface Effect {
    default void update(Duration delta) {
    }

    void process(BufferedImage input, BufferedImage output);

    boolean isEnabled();

    void setEnabled(boolean enabled);
}
