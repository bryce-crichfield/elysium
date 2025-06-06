package runtime;

import core.GameContext;
import platform.ErrorDialog;
import platform.Window;
import core.util.Util;
import game.loading.LoadingState;

import java.time.Duration;
import java.time.Instant;

public enum Runtime {
    ;
    public final static long targetUps = 60;
    public final static long targetFps = 60;

    public static void main(String[] args) throws Exception {
        GameContext gameContext = new GameContext();
        Window window = new Window(640 * 3, 480 * 3, gameContext);
        window.onInit();

        try {
            gameContext.setState(LoadingState::new);

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
                    gameContext.update(deltaUpdate);
                }

                if (dtRender > 1f / targetFps) {
                    lastRender = currentTime;
                    window.onRender(dtUpdate, dtRender);
                }
            }
        } catch (Exception e) {
            ErrorDialog.showError("An error occurred", e.getMessage());
        } finally {
            gameContext.close();
            window.onClose();
        }
    }
}
