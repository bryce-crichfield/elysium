package client.core.graphics.paint;

import client.core.graphics.Renderer;

public interface Paint {
    void apply(Renderer renderer, int x, int y, int width, int height);
}
