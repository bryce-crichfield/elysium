package game;

import game.event.EventListener;
import game.audio.Audio;
import game.graphics.background.BackgroundManager;
import game.input.Keyboard;
import game.input.Mouse;
import game.graphics.postprocessing.PostProcessingManager;
import game.graphics.postprocessing.VignetteEffect;
import game.state.GameState;
import game.state.GameStateFactory;
import game.state.GameStateManager;
import game.transition.*;
import lombok.Getter;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.Stack;

public class Game {
    public static final int SCREEN_WIDTH = 480*2;
    public static final int SCREEN_HEIGHT = 320*2;
    public static final int TILE_SIZE = 32;
    private final Keyboard keyboard = new Keyboard();
    private final Mouse mouse = new Mouse();
    private final Audio audio = new Audio();

    @Getter
    private PostProcessingManager postProcessing;

    @Getter
    private final GameStateManager stateManager;

    Game() throws Exception {
        // Initialize the state manager
        stateManager = new GameStateManager(this);

        // set volume
        audio.load(
                "resources/Shapeforms Audio Free Sound Effects/Dystopia – Ambience and Drone Preview/AUDIO/AMBIENCE_SPACECRAFT_HOLD_LOOP.wav",
                "drone.wav"
        );
////        audio.loopPlayForever("drone.wav", 0.1f);
//
        audio.load("resources/Shapeforms Audio Free Sound Effects/Cassette Preview/AUDIO/button.wav", "button.wav");
        audio.load("resources/Shapeforms Audio Free Sound Effects/future_ui/beep.wav", "caret.wav");
        audio.load("resources/Shapeforms Audio Free Sound Effects/type_preview/swipe.wav", "beep.wav");
        audio.load("resources/Shapeforms Audio Free Sound Effects/sci_fi_weapons/lock_on.wav", "select.wav");

        // Delegate keyboard events to the current game state
        Keyboard.pressed.addListener(keyCode -> {
            if (stateManager.getStateStack().isEmpty() || stateManager.isTransitioning()) {
                return;
            }

            stateManager.getCurrentState().onKeyPressed(keyCode);
        });

        EventListener<MouseEvent> dispatchMouseEventListener = event -> {
            if (stateManager.getStateStack().isEmpty() || stateManager.isTransitioning()) {
                return;
            }

            stateManager.getCurrentState().dispatchMouseEvent(event);
        };

        Mouse.moved.addListener(dispatchMouseEventListener);
        Mouse.clicked.addListener(dispatchMouseEventListener);
        Mouse.wheel.addListener(dispatchMouseEventListener);
        Mouse.dragged.addListener(dispatchMouseEventListener);
        Mouse.pressed.addListener(dispatchMouseEventListener);
        Mouse.released.addListener(dispatchMouseEventListener);

        postProcessing = new PostProcessingManager(SCREEN_WIDTH, SCREEN_HEIGHT);
        postProcessing.addProcessor(new VignetteEffect(100, 0.5f));
    }

    public Audio getAudio() {
        return audio;
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }

    public Mouse getMouse() {
        return mouse;
    }

    void onUpdate(Duration delta) {
        stateManager.update(delta);

        if (!stateManager.isTransitioning()) {
            keyboard.onUpdate();
        }
    }

    void onRender(Graphics2D graphics) {
        // Create a buffer to render the game to
        BufferedImage gameBuffer = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D bufferGraphics = gameBuffer.createGraphics();

        // Render game to buffer
        stateManager.render(bufferGraphics);

        bufferGraphics.dispose();

        // Apply post-processing effects
        BufferedImage processed = postProcessing.process(gameBuffer);

        // Draw the processed result to the screen
        graphics.drawImage(processed, 0, 0, null);
    }

    public int getWidth() {
        return SCREEN_WIDTH;
    }

    public int getHeight() {
        return SCREEN_HEIGHT;
    }
}