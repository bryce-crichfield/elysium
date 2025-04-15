package game.transition;

import game.graphics.FrameBuffer;

@FunctionalInterface
public interface TransitionFactory {
    Transition create(FrameBuffer source, FrameBuffer target, Runnable onComplete);
}