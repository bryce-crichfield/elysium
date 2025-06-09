package client.runtime.application;

import client.core.audio.Audio;
import client.core.audio.AudioStore;
import client.core.input.MouseEvent;
import client.core.graphics.postprocessing.EffectsManager;
import client.core.graphics.postprocessing.VignetteEffect;
import client.core.input.Keyboard;
import client.core.input.Mouse;
import client.core.graphics.Renderer;
import client.core.scene.ApplicationScene;
import client.core.scene.ApplicationSceneManager;
import lombok.Getter;
import lombok.experimental.Delegate;

import java.time.Duration;


// Application is where all of the stuff in /client/core is consumed.
// Client core is all of our presentation and controller scaffolding.
// This is the main entry point for the application
// The runtime is responsible for building the application and managing its lifecycle.
public class Application implements AutoCloseable {
    public static final int SCREEN_WIDTH = 480 * 2;
    public static final int SCREEN_HEIGHT = 320 * 2;
    public static final int TILE_SIZE = 32;

    private final ApplicationRuntimeContext context;

    @Getter
    private final Keyboard keyboard = new Keyboard();

    @Getter
    private final Mouse mouse = new Mouse();

    @Getter
    private final Audio audio = new Audio(AudioStore.getInstance());

    @Delegate
    private final EffectsManager effects;

    @Delegate
    private final ApplicationSceneManager stateManager;

    public Application(ApplicationRuntimeContext runtime) {
        this.context = runtime;
        stateManager = new ApplicationSceneManager(this);

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

        stateManager.getCurrentScene().onKeyPressed(keyCode);
    }

    private void dispatchMouseEvent(MouseEvent event) {
        if (!stateManager.hasState() || stateManager.isTransitioning()) {
            return;
        }

        stateManager.getCurrentScene().dispatchMouseEvent(event);
    }


    public void update(Duration delta) {
        stateManager.update(delta);

        if (!stateManager.isTransitioning()) {
            keyboard.onUpdate();
        }

        effects.update(delta);
    }

    public void render(Renderer renderer) {
        stateManager.render(renderer);
    }

    @Override
    public void close() throws Exception {
        audio.close();
    }

    public ApplicationRuntimeContext getRuntimeContext() {
        return context;
    }

    public ApplicationScene getScene() {
    if (stateManager.hasState() && !stateManager.isTransitioning()) {
            return stateManager.getCurrentScene();
        }
        return null; // No active scene or transitioning
    }
}