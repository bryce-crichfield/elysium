package game.graphics.background;

import game.platform.Renderer;

import java.awt.*;
import java.time.Duration;

public abstract class Background {
    protected int screenWidth;
    protected int screenHeight;

    public Background(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public abstract void update(Duration delta);

    public abstract void render(Renderer renderer);
}
