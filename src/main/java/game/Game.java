package game;

import game.audio.Audio;
import game.graphics.postprocessing.EffectsManager;
import game.graphics.postprocessing.VignetteEffect;
import game.input.Keyboard;
import game.input.Mouse;
import game.state.GameStateManager;
import lombok.Getter;
import lombok.experimental.Delegate;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.time.Duration;

public final class Game {
    public static final int SCREEN_WIDTH = 480 * 2;
    public static final int SCREEN_HEIGHT = 320 * 2;
    public static final int TILE_SIZE = 32;

    @Getter
    private final Keyboard keyboard = new Keyboard();

    @Getter
    private final Mouse mouse = new Mouse();

    @Getter
    private final Audio audio = new Audio();

    @Delegate
    private final EffectsManager effects;

    @Delegate
    private final GameStateManager stateManager;

    Game() throws Exception {
        stateManager = new GameStateManager(this);

        effects = new EffectsManager(SCREEN_WIDTH, SCREEN_HEIGHT);
        effects.addEffect(new VignetteEffect(100, 0.5f));

//        initializeAudio();

        Keyboard.pressed.addListener(this::dispatchKeyEvent);
        Mouse.moved.addListener(this::dispatchMouseEvent);
        Mouse.clicked.addListener(this::dispatchMouseEvent);
        Mouse.wheel.addListener(this::dispatchMouseEvent);
        Mouse.dragged.addListener(this::dispatchMouseEvent);
        Mouse.pressed.addListener(this::dispatchMouseEvent);
        Mouse.released.addListener(this::dispatchMouseEvent);
    }

    private void initializeAudio() throws Exception {
        audio.load("resources/Shapeforms Audio Free Sound Effects/Dystopia – Ambience and Drone Preview/AUDIO/AMBIENCE_SPACECRAFT_HOLD_LOOP.wav", "drone.wav");
        audio.loopPlayForever("drone.wav", 0.1f);

        audio.load("resources/Shapeforms Audio Free Sound Effects/Cassette Preview/AUDIO/button.wav", "button.wav");
        audio.load("resources/Shapeforms Audio Free Sound Effects/future_ui/beep.wav", "caret.wav");
        audio.load("resources/Shapeforms Audio Free Sound Effects/type_preview/swipe.wav", "beep.wav");
        audio.load("resources/Shapeforms Audio Free Sound Effects/sci_fi_weapons/lock_on.wav", "select.wav");
    }

    private void dispatchKeyEvent(int keyCode) {
        if (!stateManager.hasState() || stateManager.isTransitioning()) {
            return;
        }

        stateManager.getCurrentState().onKeyPressed(keyCode);
    }

    private void dispatchMouseEvent(MouseEvent event) {
        if (!stateManager.hasState() || stateManager.isTransitioning()) {
            return;
        }

        stateManager.getCurrentState().dispatchMouseEvent(event);
    }

    public void update(Duration delta) {
        stateManager.update(delta);

        if (!stateManager.isTransitioning()) {
            keyboard.onUpdate();
        }

        effects.update(delta);
    }

    public void render(Graphics2D graphics) {
        // Create a buffer to render the game to
        BufferedImage gameBuffer = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D bufferGraphics = gameBuffer.createGraphics();

        // Render game to buffer
        stateManager.render(bufferGraphics);

        bufferGraphics.dispose();

        // Apply post-processing effects
        BufferedImage processed = effects.process(gameBuffer);

        // Draw the processed result to the screen
        graphics.drawImage(processed, 0, 0, null);
    }
}