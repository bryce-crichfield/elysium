package game.graphics.postprocessing;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class PostProcessingManager {
    private List<PostProcessor> processors = new ArrayList<>();
    private BufferedImage buffer;

    public PostProcessingManager(int width, int height) {
        buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public void addProcessor(PostProcessor processor) {
        processors.add(processor);
    }

    public void removeProcessor(PostProcessor processor) {
        processors.remove(processor);
    }

    public BufferedImage process(BufferedImage input) {
        // Start with the input image
        BufferedImage current = input;

        // Apply each processor in sequence
        for (PostProcessor processor : processors) {
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

