package client.core.scene;

import client.core.graphics.FrameBuffer;
import client.core.graphics.Renderer;
import client.core.transition.Transition;
import client.core.transition.TransitionFactory;
import client.runtime.application.Application;
import java.time.Duration;
import java.util.Stack;

public class ApplicationSceneManager {
  private final Application application;
  private final Stack<ApplicationScene> states = new Stack<>();

  private Transition transition;
  private TransitionType transitionType = TransitionType.NONE;
  private ApplicationScene target; // Only used during PUSH transitions

  public ApplicationSceneManager(Application application) {
    this.application = application;
  }

  // Method to capture the current screen as a source image
  public static FrameBuffer captureScreen(ApplicationScene state) {
    var frameBuffer = new FrameBuffer(Application.SCREEN_WIDTH, Application.SCREEN_HEIGHT);
    var renderer = frameBuffer.createRenderer();
    state.render(renderer);
    frameBuffer.unbind();
    renderer.dispose();
    return frameBuffer;
  }

  // Pushes a state without transition
  public void setState(ApplicationSceneFactory factory) {
    if (!states.isEmpty()) {
      states.peek().onExit();
      states.pop();
    }

    ApplicationScene newState = factory.create(application);
    states.push(newState);
    newState.onEnter();
  }

  public void pushState(ApplicationSceneFactory factory, TransitionFactory transitionFactory) {
    if (states.isEmpty()) {
      // Special case for first state
      ApplicationScene newState = factory.create(application);
      states.push(newState);
      newState.onEnter();
      return;
    }

    if (transitionType != TransitionType.NONE) {
      throw new IllegalStateException("Already transitioning");
    }

    // Create and prepare the new state
    target = factory.create(application);

    // Capture current and target visuals
    FrameBuffer source = captureScreen(states.peek());
    FrameBuffer target = captureScreen(this.target);

    // Set up transition
    transitionType = TransitionType.PUSH;
    transition = transitionFactory.create(source, target, this::completeTransition);
  }

  public void popState(TransitionFactory transitionFactory) {
    if (states.size() <= 1) {
      // Handle basic cases
      if (!states.isEmpty()) {
        states.peek().onExit();
        states.pop();
      }
      return;
    }

    if (transitionType != TransitionType.NONE) {
      throw new IllegalStateException("Already transitioning");
    }

    // Set up the transition
    FrameBuffer source = captureScreen(states.peek());
    FrameBuffer target = captureScreen(states.get(states.size() - 2));

    transitionType = TransitionType.POP;
    transition = transitionFactory.create(source, target, this::completeTransition);
  }

  private void completeTransition() {
    switch (transitionType) {
      case PUSH:
        if (!states.isEmpty()) {
          states.peek().onExit();
        }
        states.push(target);
        target.onEnter();
        target = null;
        break;

      case POP:
        ApplicationScene oldState = states.pop();
        oldState.onExit();
        if (!states.isEmpty()) {
          states.peek().onEnter();
        }
        break;
    }

    transitionType = TransitionType.NONE;
    transition = null;
  }

  public boolean isTransitioning() {
    return transitionType != TransitionType.NONE;
  }

  public boolean hasState() {
    return !states.isEmpty();
  }

  public void update(Duration delta) {
    if (isTransitioning() && hasState()) {
      transition.update(delta);
    }

    if (!isTransitioning() && hasState()) {
      states.peek().update(delta);
    }
  }

  public void render(Renderer renderer) {
    // During transition, we let the transition handle rendering
    if (isTransitioning() && hasState()) {
      transition.render(renderer, Application.SCREEN_WIDTH, Application.SCREEN_HEIGHT);
      return;
    }

    // Normal   rendering when not transitioning
    if (!states.isEmpty()) {
      states.peek().render(renderer);
    }
  }

  public ApplicationScene getCurrentScene() {
    if (!isTransitioning() && hasState()) {
      return states.peek();
    }

    return null;
  }

  private enum TransitionType {
    NONE,
    PUSH,
    POP
  }
}
