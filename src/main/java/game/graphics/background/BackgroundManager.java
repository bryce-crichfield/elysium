package game.graphics.background;

import java.awt.*;
import java.time.Duration;
import java.util.List;
import java.util.ArrayList;

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

    public void render(Graphics2D bufferGraphics) {
        for (Background background : backgrounds) {
            background.render(bufferGraphics);
        }
    }
}
