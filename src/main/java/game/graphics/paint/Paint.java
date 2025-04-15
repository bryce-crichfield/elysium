package game.graphics.paint;

import game.graphics.Renderer;

public interface Paint {
    void apply(Renderer renderer, int x, int y, int width, int height);
}
