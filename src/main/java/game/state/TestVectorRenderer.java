package game.state;

import game.Game;
import game.graphics.Renderer;
import game.graphics.background.ShaderBackground;
import game.graphics.vector.VectorRenderer;

import java.time.Duration;

public class TestVectorRenderer extends GameState {
    private VectorRenderer vectorRenderer;

    public TestVectorRenderer(Game game) {
        super(game);
        addBackground(ShaderBackground::new);
//        vectorRenderer = new VectorRenderer();
    }

    @Override
    public void onUpdate(Duration delta) {

    }

    @Override
    public void onRender(Renderer renderer) {
//        vectorRenderer.setColor(1, 0, 0, 1);
//        vectorRenderer.fillRect(0, 0, 100, 100);
//
//        vectorRenderer.flush();
    }
}
