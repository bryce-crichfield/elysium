package game;

import game.platform.ErrorDialog;
import game.platform.Window;
import game.platform.awt.AwtWindow;
import game.platform.gl.GlWindow;
import game.state.loading.LoadingState;
import game.util.Util;

import java.time.Duration;
import java.time.Instant;

public enum Main {
    ;
    public final static long targetUps = 60;
    public final static long targetFps = 60;

    public static void main(String[] args) throws Exception {
        Game game = new Game();
        Window window = new GlWindow(640 * 3, 480 * 3, game);
        window.onInit();

        try {
            game.setState(LoadingState::new);

            Instant lastUpdate = Instant.now();
            Instant lastRender = Instant.now();

            while (window.isActive()) {
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

                    Instant renderStart = Instant.now();

                    window.onRender(dtUpdate, dtRender);
                    Instant renderEnd = Instant.now();
                    Duration renderDuration = Duration.between(renderStart, renderEnd);
                    // print ms as decimal
                    System.out.println("Render time: " + renderDuration.toMillis() + " ms");
                }
            }
        } catch (Exception e) {
            ErrorDialog.showError("An error occurred", e.getMessage());
        } finally {
            window.onClose();
        }
    }

    private static void printDebugInfo(float updateTime, float renderTime) {
        // Print the ups, fps, update time, and render time
        System.out.printf("UPS: %.1f, FPS: %.1f, Update Time: %.1f ms, Render Time: %.1f ms%n",
                1f / updateTime, 1f / renderTime, updateTime * 1000, renderTime * 1000);
    }

}
