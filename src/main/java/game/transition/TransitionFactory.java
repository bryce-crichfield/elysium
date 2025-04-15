package game.transition;

import game.platform.FrameBuffer;

@FunctionalInterface
public interface TransitionFactory {
    Transition create(FrameBuffer source, FrameBuffer target, Runnable onComplete);
}