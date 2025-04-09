package game.state;

import game.Game;
import game.transition.Transition;
import game.transition.TransitionFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.Stack;

public class GameStateManager {
    private final Game game;
    private final Stack<GameState> states = new Stack<>();

    private Transition transition;
    private TransitionType transitionType = TransitionType.NONE;
    private GameState target; // Only used during PUSH transitions
    public GameStateManager(Game game) {
        this.game = game;
    }

    // Method to capture the current screen as a source image
    public static BufferedImage captureScreen(GameState state) {
        var image = new BufferedImage(Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        var g = image.createGraphics();
        state.render(g);
        g.dispose();
        return image;
    }

    // Pushes a state without transition
    public void setState(GameStateFactory factory) {
        if (!states.isEmpty()) {
            states.peek().onExit();
            states.pop();
        }

        GameState newState = factory.create(game);
        states.push(newState);
        newState.onEnter();
    }

    public void pushState(GameStateFactory factory, TransitionFactory transitionFactory) {
        System.out.println("PUSHING STATE");
        if (states.isEmpty()) {
            // Special case for first state
            GameState newState = factory.create(game);
            states.push(newState);
            newState.onEnter();
            return;
        }

        if (transitionType != TransitionType.NONE) {
            throw new IllegalStateException("Already transitioning");
        }

        // Create and prepare the new state
        target = factory.create(game);

        // Capture current and target visuals
        BufferedImage source = captureScreen(states.peek());
        BufferedImage target = captureScreen(this.target);

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
        BufferedImage source = captureScreen(states.peek());
        BufferedImage target = captureScreen(states.get(states.size() - 2));

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
                GameState oldState = states.pop();
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

    public void render(Graphics2D graphics) {
        // During transition, we let the transition handle rendering
        if (isTransitioning() && hasState()) {
            transition.render(graphics, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
            return;
        }

        // Normal rendering when not transitioning
        if (!states.isEmpty()) {
            states.peek().render(graphics);
        }
    }

    public GameState getCurrentState() {
        if (!isTransitioning() && hasState()) {
            return states.peek();
        }

        return null;
    }

    private enum TransitionType {NONE, PUSH, POP}
}