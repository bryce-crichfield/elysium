package game.state.loading;

import game.Game;
import game.asset.AssetLoader;
import game.audio.AudioLoader;
import game.audio.AudioStore;
import game.gui.GuiComponent;
import game.graphics.Renderer;
import game.graphics.texture.TextureLoader;
import game.graphics.texture.TextureStore;
import game.state.GameState;
import game.state.battle.BattleState;
import game.transition.Transitions;
import game.util.Easing;

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

    public LoadingState(Game game) {
        super(game);
        var audioLoader = new AudioLoader(AudioStore.getInstance(), Path.of("resources/audio"), game.getAudio().getFormat());
        completedAssetLoaders.put(audioLoader, false);
        loadQueue.add(audioLoader);

        var textureLoader = new TextureLoader(TextureStore.getInstance(), Path.of("resources/texture"));
        completedAssetLoaders.put(textureLoader, false);
        loadQueue.add(textureLoader);

        progressBar = createProgressBar(loadQueue);
    }

    private static GuiComponent createProgressBar(Collection<AssetLoader> loaders) {
        final GuiComponent progressBar;
        var width = (int) (Game.SCREEN_WIDTH * 0.75f);
        var height = 25;
        var x = (Game.SCREEN_WIDTH - width) / 2;
        var y = (Game.SCREEN_HEIGHT - (4 * height));
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
        game.pushState(BattleState::new,
                Transitions.fade(Duration.ofMillis(1000), Color.BLACK, Easing.cubicEaseIn()));
    }

    @Override
    public void onRender(Renderer renderer) {
        progressBar.render(renderer);

        // Draw the Message String
        if (!loadQueue.isEmpty() && loadQueue.peek() != null) {
            renderer.setFont(new Font("/fonts/arial", Font.BOLD, 12));
            renderer.setColor(Color.WHITE);

            var message = loadQueue.peek().getDebugMessage();
            var fontInfo = renderer.getFontInfo();
            var textWidth = fontInfo.getStringWidth(message);
            var textX = (Game.SCREEN_WIDTH - textWidth) / 2;
            var textY = progressBar.getY() - fontInfo.getHeight() - 5;
            renderer.drawString(message, textX, textY);
        }
    }
}
