package game.state;

import game.Game;
import game.transition.Transition;
import game.transition.TransitionFactory;
import game.transition.PixelateTransition;
import lombok.Getter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.Stack;

public class GameStateManager {
        private final Game game;
        @Getter
        private final Stack<GameState> stateStack = new Stack<>();
        private Transition transition;
        private boolean isTransitioning = false;

        // Add these to track states during transition
        private GameState currentVisibleState;
        private GameState pendingState;
        private boolean isPoppingState = false;

        public GameStateManager(Game game) {
            this.game = game;
        }

        public void pushState(GameStateFactory stateFactory, TransitionFactory transitionFactory) {
            if (stateStack.isEmpty()) {
                var state = stateFactory.create(game);
                stateStack.push(state);
                currentVisibleState = state;
                state.onEnter();
                return;
            }

            // Already in transition, ignore
            if (isTransitioning) {
                return;
            }

            // Get current state
            GameState oldState = stateStack.peek();
            currentVisibleState = oldState;

            // Create the new state
            GameState newState = stateFactory.create(game);
            pendingState = newState;

            // Capture the current screen
            BufferedImage sourceImage = PixelateTransition.captureScreen(game);

            // Temporarily push and setup new state for screenshot
            stateStack.push(newState);
            newState.onEnter();

            // Capture what the new state would look like
            BufferedImage targetImage = PixelateTransition.captureScreen(game);

            // Restore stack to original state
            stateStack.pop();

            // Start the transition
            isTransitioning = true;
            isPoppingState = false;

            // Create the transition with duration parameter
            transition = transitionFactory.create(
                    sourceImage,
                    targetImage,
                    this::completeTransition
            );
        }

        public void popState(TransitionFactory transitionFactory) {
            if (stateStack.isEmpty() || stateStack.size() == 1) {
                // Either no states or only one state (can't pop the last state)
                if (!stateStack.isEmpty()) {
                    stateStack.peek().onExit();
                    stateStack.pop();
                }
                return;
            }

            // Already in transition, ignore
            if (isTransitioning) {
                return;
            }

            // Store reference to current state that will be popped
            GameState topState = stateStack.peek();
            currentVisibleState = topState;

            // Capture the current screen
            BufferedImage sourceImage = PixelateTransition.captureScreen(game);

            // Temporarily set up the stack to look like after the pop
            stateStack.pop();
            GameState previousState = stateStack.peek();
            pendingState = previousState;
            previousState.onEnter();

            // Capture what the previous state looks like
            BufferedImage targetImage = PixelateTransition.captureScreen(game);

            // Put the stack back how it was
            previousState.onExit();
            stateStack.push(topState);

            // Start the transition
            isTransitioning = true;
            isPoppingState = true;

            // Create transition with duration parameter
            transition = transitionFactory.create(
                    sourceImage,
                    targetImage,
                    this::completeTransition
            );
        }

        private void completeTransition() {
            isTransitioning = false;

            if (isPoppingState) {
                // Handle pop state completion
                GameState topState = stateStack.pop();
                topState.onExit();

                if (!stateStack.isEmpty()) {
                    GameState previousState = stateStack.peek();
                    previousState.onEnter();
                    currentVisibleState = previousState;
                }
            } else {
                // Handle push state completion
                if (pendingState != null) {
                    if (currentVisibleState != null) {
                        currentVisibleState.onExit();
                    }
                    stateStack.push(pendingState);
                    pendingState.onEnter();
                    currentVisibleState = pendingState;
                    pendingState = null;
                }
            }
        }

        public boolean isTransitioning() {
            return isTransitioning;
        }

        public void update(Duration delta) {
            if (isTransitioning && transition != null) {
                transition.update(delta);
            }

            if (!isTransitioning && !stateStack.isEmpty()) {
                stateStack.peek().update(delta);
            } else if (currentVisibleState != null) {
                // During transition, update the visible state
                currentVisibleState.update(delta);
            }
        }

        public void render(Graphics2D graphics) {


            // During transition, we let the transition handle rendering
            if (isTransitioning && transition != null) {
                transition.render(graphics, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
                return;
            }

            // Normal rendering when not transitioning
            if (!stateStack.isEmpty()) {
                stateStack.peek().render(graphics);
            }
        }

        public GameState getCurrentState() {
            return isTransitioning ? currentVisibleState :
                    (stateStack.isEmpty() ? null : stateStack.peek());
        }
}