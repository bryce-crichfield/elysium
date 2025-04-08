package game.transition;

import java.awt.image.BufferedImage;

@FunctionalInterface
public interface TransitionFactory {
    Transition create(BufferedImage source, BufferedImage target, Runnable onComplete);
}