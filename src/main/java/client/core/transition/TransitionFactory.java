package client.core.transition;

import client.core.graphics.FrameBuffer;

@FunctionalInterface
public interface TransitionFactory {
  Transition create(FrameBuffer source, FrameBuffer target, Runnable onComplete);
}
