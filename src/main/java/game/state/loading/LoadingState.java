package game.state.loading;

import game.Game;
import game.asset.AssetLoader;
import game.audio.AudioAssetLoader;
import game.audio.AudioStore;
import game.gui.GuiComponent;
import game.gui.GuiContainer;
import game.platform.Renderer;
import game.platform.gl.GlFrameBuffer;
import game.platform.gl.GlTransform;
import game.state.GameState;
import game.state.battle.BattleState;
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

import static org.lwjgl.opengl.GL11.*;

public class LoadingState extends GameState {
    private final Queue<AssetLoader> loadQueue = new ConcurrentLinkedQueue<>();
    private final Map<AssetLoader, Boolean> assetLoaders = new HashMap<>();

    private final GuiComponent container;

    public LoadingState(Game game) {
        super(game);
        var assetLoader = new AudioAssetLoader(game.getAudio().getAudioStore(), Path.of("resources/audio"), game.getAudio().getFormat());
        assetLoaders.put(assetLoader, false);
        loadQueue.add(assetLoader);


        // create a progress bar component
        var width = (int)(Game.SCREEN_WIDTH * 0.75f);
        var height = 25;
        var x = (Game.SCREEN_WIDTH - width) / 2;
        var y = (Game.SCREEN_HEIGHT - (4*height));
        container = new GuiComponent(x, y, width, height) {
            @Override
            protected void onRender(Renderer g) {
                g.setColor(Color.BLUE);
                var drawWidth = (int) (width * getTotalLoadingProgress());
                System.out.println("drawWidth = " + drawWidth);
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
    public void onRender(Renderer renderer) {
        container.render(renderer);

        // print the contents of the resources root folder
        renderer.setFont(new Font("/fonts/arial", Font.BOLD, 12));
        renderer.setColor(Color.WHITE);
        renderer.drawString("Loading...", Game.SCREEN_WIDTH / 2 - 50, Game.SCREEN_HEIGHT / 2 - 50);

//        var frameBuffer = new GlFrameBuffer(Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
//        var r = frameBuffer.createRenderer();
//        // draw black and white checkers
//        r.setColor(Color.BLACK);
//        for (int i = 0; i < Game.SCREEN_WIDTH; i += 10) {
//            for (int j = 0; j < Game.SCREEN_HEIGHT; j += 10) {
//                // determine the color based on the position
//                if ((i / 10 + j / 10) % 2 == 0) {
//                    r.setColor(Color.WHITE);
//                } else {
//                    r.setColor(Color.BLACK);
//                }
//                r.fillRect(i, j, 10, 10);
//            }
//        }
//
//        r.dispose();
//        frameBuffer.unbind();
//        renderer.drawFrameBuffer(frameBuffer, 0, 0, 400, 400);
    }

}
