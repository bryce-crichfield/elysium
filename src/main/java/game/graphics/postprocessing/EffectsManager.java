package game.graphics.postprocessing;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class EffectsManager {
    private final List<Effect> effects = new ArrayList<>();
    private final BufferedImage buffer;

    public EffectsManager(int width, int height) {
        buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public void addEffect(Effect processor) {
        effects.add(processor);
    }

    public void removeEffect(Effect processor) {
        effects.remove(processor);
    }

    public void clearEffects() {
        effects.clear();
    }

    public void update(Duration delta) {
        for (Effect processor : effects) {
            if (processor.isEnabled()) {
                processor.update(delta);
            }
        }
    }

    public BufferedImage process(BufferedImage input) {
        // Start with the input image
        BufferedImage current = input;

        // Apply each processor in sequence
        for (Effect processor : effects) {
            if (processor.isEnabled()) {
                // Clear buffer
                Graphics2D g = buffer.createGraphics();
                g.setComposite(AlphaComposite.Clear);
                g.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
                g.dispose();

                // Process the current image into the buffer
                processor.process(current, buffer);

                // Update current to the processed result
                current = buffer;
            }
        }

        return current;
    }
}

