package game.transition;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class CompositeTransition extends Transition {
    private final List<Transition> transitions = new ArrayList<>();

    public CompositeTransition(Duration duration) {
        super(duration);
    }

    public void addTransition(Transition transition) {
        transitions.add(transition);
    }

    @Override
    public void update(Duration delta) {
        super.update(delta);

        // Update all child transitions with the same delta
        for (Transition transition : transitions) {
            transition.update(delta);
        }
    }

    @Override
    public void render(Graphics2D graphics, int width, int height) {
        // Create a temporary image to render each layer
        BufferedImage tempImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D tempGraphics = tempImage.createGraphics();

        // Render each transition to the temporary graphics
        for (Transition transition : transitions) {
            transition.render(tempGraphics, width, height);
        }

        tempGraphics.dispose();

        // Draw the combined result to the main graphics
        graphics.drawImage(tempImage, 0, 0, null);
    }
}
