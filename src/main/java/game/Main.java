package game;

import game.platform.ErrorDialog;
import game.platform.Window;
import game.state.title.TitleState;
import game.transition.Transitions;
import game.util.Easing;
import game.util.Util;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;

public enum Main {
    ;
    public final static long targetUps = 60;
    public final static long targetFps = 60;

    public static void main(String[] args) throws Exception {
        Game game = new Game();
        Window window = new Window(640 * 3, 480 * 3, game);

        try {
            execute(game, window);
        } catch (Exception e) {
            ErrorDialog.showError("An error occurred", e.getMessage());
        } finally {
            window.dispose();
        }
    }

    public static void execute(Game game, Window window) throws Exception {
        game.pushState(TitleState::new, Transitions.fade(Duration.ofMillis(1000), Color.BLACK, Easing.cubicEaseIn()));

        Instant lastUpdate = Instant.now();
        Instant lastRender = Instant.now();

        while (true) {
            Instant currentTime = Instant.now();

            Duration deltaUpdate = Duration.between(lastUpdate, currentTime);
            Duration deltaRender = Duration.between(lastRender, currentTime);

            float dtUpdate = Util.perSecond(deltaUpdate);
            float dtRender = Util.perSecond(deltaRender);

            if (dtUpdate > 1f / targetUps) {
                lastUpdate = currentTime;
                game.update(deltaUpdate);
            }

            if (dtRender > 1f / targetFps) {
                lastRender = currentTime;
                window.onRender(dtUpdate, dtRender);
            }
        }
    }
}
