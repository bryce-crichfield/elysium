package game.platform.paint;

import game.platform.Renderer;

public interface Paint {
    void apply(Renderer renderer, int x, int y, int width, int height);
}
