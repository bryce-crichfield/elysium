package client.runtime;

import client.runtime.application.ApplicationFactory;
import client.core.graphics.platform.ErrorDialog;
import client.core.graphics.platform.Window;
import client.core.scene.ApplicationSceneFactory;
import client.core.util.Util;
import client.runtime.application.ApplicationRuntimeContext;
import client.runtime.config.RuntimeArguments;
import client.runtime.system.SystemRuntimeContext;
import client.runtime.system.Systems;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;

public class RuntimeContainer {
    private final RuntimeArguments arguments;

    @Getter
    private final Systems systems = new Systems();

    // Used to delay initialization of the client until the window is created
    private final ApplicationFactory applicationFactory;
    private final ApplicationSceneFactory initialState;

    public RuntimeContainer(RuntimeArguments arguments, ApplicationFactory applicationFactory, ApplicationSceneFactory initialStateFactory) {
        this.arguments = arguments;
        this.applicationFactory = applicationFactory;
        this.initialState = initialStateFactory;
    }

    public void start() throws Exception {
        var application = applicationFactory.create(new ApplicationRuntimeContext(systems, arguments));

        systems.start(arguments, new SystemRuntimeContext(application));

        Window window = new Window(640 * 3, 480 * 3, application);
        window.onInit();

        String sTargetUps = arguments.getOrDefault("targetUps", "60");
        long targetUps = Long.parseLong(sTargetUps);

        String sTargetFps = arguments.getOrDefault("targetFps", "60");
        long targetFps = Long.parseLong(sTargetFps);

        try {
            application.setState(initialState);

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

                    application.update(deltaUpdate);
                }

                if (dtRender > 1f / targetFps) {
                    lastRender = currentTime;
                    window.onRender(dtUpdate, dtRender);
                }
            }
        } catch (Exception e) {
            ErrorDialog.showError("An error occurred", e.getMessage());
        } finally {
            application.close();

            window.onClose();
        }
    }
}
