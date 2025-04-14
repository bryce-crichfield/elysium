package game.transition;

import game.platform.FrameBuffer;

import java.awt.image.BufferedImage;

@FunctionalInterface
public interface TransitionFactory {
    Transition create(FrameBuffer source, FrameBuffer target, Runnable onComplete);
}