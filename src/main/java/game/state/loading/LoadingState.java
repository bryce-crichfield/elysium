package game.state.loading;

import game.Game;
import game.asset.AssetLoader;
import game.audio.AudioAssetLoader;
import game.audio.AudioStore;
import game.gui.GuiComponent;
import game.gui.GuiContainer;
import game.state.GameState;
import game.state.title.TitleState;
import game.transition.Transitions;
import game.util.Easing;

import java.awt.*;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LoadingState extends GameState {
    private final Queue<AssetLoader> loadQueue = new ConcurrentLinkedQueue<>();
    private final Map<AssetLoader, Boolean> assetLoaders = new HashMap<>();

    private final GuiComponent container;

    public LoadingState(Game game) {
        super(game);
        var assetLoader = new AudioAssetLoader(game.getAudio().getAudioStore(), Path.of("resources/audio"));
        assetLoaders.put(assetLoader, false);
        loadQueue.add(assetLoader);


        // create a progress bar component
        var width = (int)(Game.SCREEN_WIDTH * 0.75f);
        var height = 25;
        var x = (Game.SCREEN_WIDTH - width) / 2;
        var y = (Game.SCREEN_HEIGHT - (4*height));
        container = new GuiComponent(x, y, width, height) {
            @Override
            protected void onRender(Graphics2D g) {
                g.setColor(Color.BLUE);
                var drawWidth = (int) (width * getTotalLoadingProgress());
                g.fillRect(0, 0, drawWidth, height);
                g.setColor(Color.WHITE);
                g.drawRect(0, 0, width, height);
            }
        };
    }

    private float getTotalLoadingProgress() {
        // each loader returns 0-1
        // and we can average them
        float totalProgress = 0;
        for (var loader : assetLoaders.keySet()) {
            totalProgress += loader.getProgress();
        }
        return totalProgress / assetLoaders.size();
    }

    @Override
    public void onUpdate(Duration delta) {
        // peek the top of the queue and if its not started loading, load it, once its complete mark it as complete and
        // pop it from the queue
        if (!loadQueue.isEmpty()) {
            var loader = loadQueue.peek();
            if (!assetLoaders.get(loader)) {
                assetLoaders.put(loader, true);
                //                    assetLoaders.put(loader, false);
                loader.load(loadQueue::poll);
            }
        }

        if (loadQueue.isEmpty()) {
            // all loaders are done, we can move to the next state
            game.pushState(TitleState::new, Transitions.fade(Duration.ofMillis(1000), Color.BLACK, Easing.cubicEaseIn()));
        }


        container.update(delta);
    }

    @Override
    public void onRender(Graphics2D graphics) {
        container.render(graphics);
    }
}
