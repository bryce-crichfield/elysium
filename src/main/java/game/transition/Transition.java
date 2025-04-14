package game.transition;

import game.platform.Renderer;

import java.awt.*;
import java.time.Duration;
import java.util.function.Consumer;

public abstract class Transition {
    protected Duration duration;
    protected Duration elapsed = Duration.ZERO;
    protected boolean isComplete = false;
    protected Consumer<Boolean> onCompleteCallback;

    public Transition(Duration duration) {
        this.duration = duration;
    }

    public void update(Duration delta) {
        elapsed = elapsed.plus(delta);
        if (elapsed.compareTo(duration) >= 0) {
            isComplete = true;
            if (onCompleteCallback != null) {
                onCompleteCallback.accept(true);
            }
        }
    }

    public abstract void render(Renderer graphics, int width, int height);

    public void setOnCompleteCallback(Consumer<Boolean> callback) {
        this.onCompleteCallback = callback;
    }

    // Helper to get progress as a float from 0.0 to 1.0
    protected float getProgress() {
        return Math.min(1.0f, (float) elapsed.toMillis() / duration.toMillis());
    }
}
