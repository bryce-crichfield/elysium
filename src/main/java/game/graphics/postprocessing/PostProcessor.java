package game.graphics.postprocessing;

import java.awt.image.BufferedImage;

public interface PostProcessor {
    void process(BufferedImage input, BufferedImage output);
    boolean isEnabled();
    void setEnabled(boolean enabled);
}
