package client.core.graphics.background;

import client.core.graphics.Renderer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class BackgroundManager {
    private final int screenWidth;
    private final int screenHeight;
    private final List<Background> backgrounds = new ArrayList<>();

    public BackgroundManager(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void add(BackgroundFactory background) {
        Background bg = background.create(screenWidth, screenHeight);
        if (bg != null) {
            backgrounds.add(bg);
        }
    }

    public void clear() {
        backgrounds.clear();
    }

    public void update(Duration delta) {
        for (Background background : backgrounds) {
            background.update(delta);
        }
    }

    public void render(Renderer bufferGraphics) {
        for (Background background : backgrounds) {
            background.render(bufferGraphics);
        }
    }
}
