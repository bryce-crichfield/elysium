package game.state.overworld.entity;

import game.platform.Renderer;

import java.awt.*;
import java.util.Optional;

public class Tile {
    private final int x;
    private final int y;
    private Optional<String> exitId = Optional.empty();

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isExit() {
        return exitId.isPresent();
    }

    public void setExit(String exitId) {
        this.exitId = Optional.of(exitId);
    }

    public String getExitId() {
        return exitId.orElseThrow(() -> new IllegalStateException("Tile is not an exit"));
    }

    public void onRender(Renderer renderer) {
        renderer.setColor(Color.GREEN);
        if (isExit()) {
            renderer.setColor(Color.YELLOW);
        }
        renderer.fillRect(x, y, 32, 32);
        renderer.setColor(Color.BLACK);
        renderer.drawRect(x, y, 32, 32);
    }

    public int getWorldX() {
        return x / 32;
    }

    public int getWorldY() {
        return y / 32;
    }
}
