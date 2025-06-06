package game.loading;

import core.GameContext;
import core.asset.AssetLoader;
import core.audio.AudioLoader;
import core.audio.AudioStore;
import core.graphics.Renderer;
import core.graphics.texture.TextureLoader;
import core.graphics.texture.TextureStore;
import core.gui.GuiComponent;
import core.state.GameState;
import game.battle.BattleState;
import core.transition.Transitions;
import core.util.Easing;

import java.awt.*;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LoadingState extends GameState {
    private final Queue<AssetLoader> loadQueue = new ConcurrentLinkedQueue<>();
    private final Map<AssetLoader, Boolean> completedAssetLoaders = new HashMap<>();

    private final GuiComponent progressBar;

    public LoadingState(GameContext gameContext) {
        super(gameContext);
        var audioLoader = new AudioLoader(AudioStore.getInstance(), Path.of("resources/audio"), gameContext.getAudio().getFormat());
        completedAssetLoaders.put(audioLoader, false);
        loadQueue.add(audioLoader);

        var textureLoader = new TextureLoader(TextureStore.getInstance(), Path.of("resources/texture"));
        completedAssetLoaders.put(textureLoader, false);
        loadQueue.add(textureLoader);

        progressBar = createProgressBar(loadQueue);
    }

    private static GuiComponent createProgressBar(Collection<AssetLoader> loaders) {
        final GuiComponent progressBar;
        var width = (int) (GameContext.SCREEN_WIDTH * 0.75f);
        var height = 25;
        var x = (GameContext.SCREEN_WIDTH - width) / 2;
        var y = (GameContext.SCREEN_HEIGHT - (4 * height));
        progressBar = new GuiComponent(x, y, width, height) {
            @Override
            protected void onRender(Renderer g) {
                g.setColor(Color.BLUE);
                // Average the progress of all loaders
                float totalProgress = 0;
                for (var loader : loaders) {
                    totalProgress += loader.getProgress();
                }
                var drawWidth = (int) (width * (totalProgress / loaders.size()));
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

        // Handle case when there are still loaders in the queue
        if (!loadQueue.isEmpty()) {
            var loader = loadQueue.peek();
            if (!completedAssetLoaders.get(loader)) {
                completedAssetLoaders.put(loader, true);
                // The loader will remove itself from the queue when it is done
                loader.load(loadQueue::poll);
            }
            return;
        }

        // All loaders are done, initialize textures
        for (var key : TextureStore.getInstance().getAssets()) {
            var texture = TextureStore.getInstance().getAssets(key);
            texture.initialize();
        }

        // Transition to the next state
        gameContext.pushState(BattleState::new,
                Transitions.fade(Duration.ofMillis(1000), Color.BLACK, Easing.cubicEaseIn()));
    }

    @Override
    public void onRender(Renderer renderer) {
        progressBar.render(renderer);

        // Draw the Message String
        if (!loadQueue.isEmpty() && loadQueue.peek() != null) {
            renderer.setFont("/fonts/arial", 12);
            renderer.setColor(Color.WHITE);

            var message = loadQueue.peek().getDebugMessage();
            var fontInfo = renderer.getFontInfo();
            var textWidth = fontInfo.getStringWidth(message);
            var textX = (GameContext.SCREEN_WIDTH - textWidth) / 2;
            var textY = progressBar.getY() - fontInfo.getHeight() - 5;
            renderer.drawString(message, textX, textY);
        }
    }
}
