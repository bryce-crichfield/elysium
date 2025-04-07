package game;

import game.event.Event;
import game.io.Audio;
import game.io.Keyboard;
import game.io.Mouse;
import game.state.GameState;


import java.awt.*;
import java.time.Duration;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;

public class Game {
    public static final int SCREEN_WIDTH = 480*2;
    public static final int SCREEN_HEIGHT = 320*2;
    public static final int TILE_SIZE = 32;
    private final Keyboard keyboard = new Keyboard();
    private final Mouse mouse = new Mouse();
    private final Audio audio = new Audio();
    private final Stack<GameState> stateStack = new Stack<>();

    Game() throws Exception {
        // set volume
        // set volume
        audio.load(
                "resources/Shapeforms Audio Free Sound Effects/Dystopia – Ambience and Drone Preview/AUDIO/AMBIENCE_SPACECRAFT_HOLD_LOOP.wav",
                "drone.wav"
        );
//        audio.loopPlayForever("drone.wav", 0.1f);

        audio.load("resources/Shapeforms Audio Free Sound Effects/Cassette Preview/AUDIO/button.wav", "button.wav");
        audio.load("resources/Shapeforms Audio Free Sound Effects/future_ui/beep.wav", "caret.wav");
        audio.load("resources/Shapeforms Audio Free Sound Effects/type_preview/swipe.wav", "beep.wav");
        audio.load("resources/Shapeforms Audio Free Sound Effects/sci_fi_weapons/lock_on.wav", "select.wav");


        // Delegate keyboard events to the current game state
        Keyboard.pressed.addListener(keyCode -> {
            if (stateStack.isEmpty()) {
                return;
            }

            stateStack.peek().onKeyPressed(keyCode);
        });

        Mouse.moved.addListener(event -> {
            if (stateStack.isEmpty()) {
                return;
            }

            stateStack.peek().onMouseMoved(event);
        });

        Mouse.clicked.addListener(event -> {
            if (stateStack.isEmpty()) {
                return;
            }

            stateStack.peek().onMouseClicked(event);
        });

        Mouse.wheel.addListener(event -> {
            if (stateStack.isEmpty()) {
                return;
            }

            stateStack.peek().onMouseWheelMoved(event);
        });
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

    public void pushState(Function<Game, GameState> factory) {
        if (!stateStack.isEmpty()) {
            stateStack.peek().onExit();
        }

        var state = factory.apply(this);
        stateStack.push(state);
        state.onEnter();
    }

    public void popState() {
        if (!stateStack.isEmpty()) {
            stateStack.peek().onExit();
            stateStack.pop();
        }

        if (!stateStack.isEmpty()) {
            stateStack.peek().onEnter();
        }
    }

    void onUpdate(Duration delta) {
        if (!stateStack.isEmpty()) {
            stateStack.peek().onUpdate(delta);
        }

        keyboard.onUpdate();
    }

    void onRender(Graphics2D graphics) {
        if (!stateStack.isEmpty()) {
            stateStack.peek().onRender(graphics);
        }
    }
}
