package game.transition;

import game.util.Easing;

import java.awt.*;
import java.time.Duration;

public class FadeTransition extends Transition {
    private final Color color;
    private final Easing easing;

    public FadeTransition(Duration duration, Color color, Easing easing) {
        super(duration);
        this.color = color;
        this.easing = easing;
    }

    @Override
    public void render(Graphics2D graphics, int width, int height) {
        float progress = getProgress() * duration.toMillis();
        float alpha = easing.ease(0, 1, duration.toMillis(), progress);

        Composite originalComposite = graphics.getComposite();
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        graphics.setColor(color);
        graphics.fillRect(0, 0, width, height);
        graphics.setComposite(originalComposite);
    }
}