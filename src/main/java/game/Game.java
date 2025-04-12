package game;

import game.audio.Audio;
import game.audio.AudioEngine;
import game.input.MouseEvent;
import game.graphics.postprocessing.EffectsManager;
import game.graphics.postprocessing.VignetteEffect;
import game.input.Keyboard;
import game.input.Mouse;
import game.platform.FrameBuffer;
import game.platform.Renderer;
import game.state.GameStateManager;
import lombok.Getter;
import lombok.experimental.Delegate;

import java.awt.*;
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

        Keyboard.pressed.addListener(this::dispatchKeyEvent);
        Mouse.moved.addListener(this::dispatchMouseEvent);
        Mouse.clicked.addListener(this::dispatchMouseEvent);
        Mouse.wheel.addListener(this::dispatchMouseEvent);
        Mouse.dragged.addListener(this::dispatchMouseEvent);
        Mouse.pressed.addListener(this::dispatchMouseEvent);
        Mouse.released.addListener(this::dispatchMouseEvent);
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

    public void render(Renderer renderer) {
        // Create a buffer to render the game to
//        FrameBuffer buffer = renderer.createFrameBuffer(SCREEN_WIDTH, SCREEN_HEIGHT);
//        Renderer bufferRenderer = buffer.createRenderer();
//        BufferedImage gameBuffer = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
//        Graphics2D bufferGraphics = gameBuffer.createGraphics();

        // Render game to buffer
        stateManager.render(renderer);

//        bufferRenderer.dispose();

//        renderer.drawFrameBuffer(buffer, 0, 0);

        // Apply post-processing effects
//        BufferedImage processed = effects.process(gameBuffer);

        // Draw the processed result to the screen
//        graphics.drawImage(processed, 0, 0, null);
    }
}