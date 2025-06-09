package sampleGame.loading;

import client.runtime.application.Application;
import client.core.audio.AudioLoader;
import client.core.audio.AudioStore;
import client.core.graphics.Renderer;
import client.core.graphics.texture.TextureLoader;
import client.core.graphics.texture.TextureStore;
import client.core.gui.GuiComponent;
import client.core.scene.ApplicationScene;
import client.runtime.application.ApplicationRuntimeContext;
import client.runtime.system.SystemRuntimeContext;
import client.runtime.system.loading.LoadingStage;
import client.runtime.system.loading.LoadingSystem;
import client.runtime.system.loading.LoadingThread;
import client.runtime.system.networking.NetworkingSystem;
import sampleGame.battle.BattleScene;
import client.core.transition.Transitions;
import client.core.util.Easing;
import client.runtime.system.loading.stages.AssetLoadingStage;
import client.runtime.system.loading.stages.SystemLoadingStage;

import java.awt.*;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.List;

public class LoadingScene extends ApplicationScene {
    private final GuiComponent progressBar;

    public LoadingScene(Application game) {
        super(game);

        progressBar = createProgressBar(getApplication().getRuntimeContext());
    }

    public void onEnter() {
        super.onEnter();

        // ensure the loading system is initialized
        var loadingSystem = getApplication().getRuntimeContext().getSystem(LoadingSystem.class);
        if (loadingSystem == null || !loadingSystem.isInitialized()) {
            throw new IllegalStateException("LoadingSystem must be initialized before loading scene.");
        }

        List<LoadingStage> stages = new ArrayList<>();
        var audioLoader = new AudioLoader(AudioStore.getInstance(), Path.of("resources/audio"), application.getAudio().getFormat());
        stages.add(new AssetLoadingStage(audioLoader));

        var textureLoader = new TextureLoader(TextureStore.getInstance(), Path.of("resources/texture"));
        stages.add(new AssetLoadingStage(textureLoader));

        var networkLoader = new SystemLoadingStage<NetworkingSystem>(
                this.getApplication(),
                NetworkingSystem.class
        );
        stages.add(networkLoader);

        loadingSystem.queueStages(stages);

        // Reset progress bar
        progressBar.setVisible(true);
        progressBar.update(Duration.ZERO);
    }

    private static GuiComponent createProgressBar(ApplicationRuntimeContext context) {
        final GuiComponent progressBar;
        var width = (int) (Application.SCREEN_WIDTH * 0.75f);
        var height = 25;
        var x = (Application.SCREEN_WIDTH - width) / 2;
        var y = (Application.SCREEN_HEIGHT - (4 * height));
        progressBar = new GuiComponent(x, y, width, height) {
            @Override
            protected void onRender(Renderer g) {
                // if the loading system is not initialized, don't render the progress bar
                var loadingSystem = context.getSystem(LoadingSystem.class);
                if (loadingSystem == null || !loadingSystem.isInitialized()) {
                    setVisible(false);
                    return;
                }

                g.setColor(Color.BLUE);
                var drawWidth = (int) (width * loadingSystem.getProgress());
                g.fillRect(0, 0, drawWidth, height);
                g.setColor(Color.WHITE);
                g.drawRect(0, 0, width, height);
            }

            @Override
            protected String getComponentName() {
                return "";
            }
        };
        return progressBar;
    }

    @Override
    public void onUpdate(Duration delta) {
        progressBar.update(delta);

        var loadingSystem = getApplication().getRuntimeContext().getSystem(LoadingSystem.class);
        if (!loadingSystem.isComplete()) {
            return; // still loading
        }

        // All loaders are done, initialize textures
        for (var key : TextureStore.getInstance().getAssets()) {
            var texture = TextureStore.getInstance().getAssets(key);
            texture.initialize();
        }

        // Transition to the next state
        application.pushState(BattleScene::new,
                Transitions.fade(Duration.ofMillis(1000), Color.BLACK, Easing.cubicEaseIn()));
    }

    @Override
    public void onRender(Renderer renderer) {
        progressBar.render(renderer);

        renderer.setFont("/fonts/arial", 12);
        renderer.setColor(Color.WHITE);

        var loadingSystem = getApplication().getRuntimeContext().getSystem(LoadingSystem.class);
        var message = loadingSystem.getLoadingMessage();
        var fontInfo = renderer.getFontInfo();
        var textWidth = fontInfo.getStringWidth(message);
        var textX = (Application.SCREEN_WIDTH - textWidth) / 2;
        var textY = progressBar.getY() - fontInfo.getHeight() - 5;
        renderer.drawString(message, textX, textY);
    }

    @Override
    public void onExit() {
        super.onExit();

        // stop the loading thread if it is still running
//        if (loadingThread != null && loadingThread.isAlive()) {
//            loadingThread.interrupt();
//        }
    }
}
