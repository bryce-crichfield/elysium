package game.state.overworld;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Frame {
    private Tile[][] tiles;
    private int width;
    private int height;

    public Frame(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = new Tile(x * 32, y * 32);
            }
        }
    }

    public Optional<Tile> getIntersectingTile(float x, float y, float width, float height) {
        // check if the provided rectangle intersects with any tile...
        // if it intersects with more than one tile, return the first one with largest area
        Map<Tile, Integer> intersections = new HashMap<>();
        for (int tileX = 0; tileX < this.width; tileX++) {
            for (int tileY = 0; tileY < this.height; tileY++) {
                Tile tile = tiles[tileX][tileY];
                Rectangle tileRect = new Rectangle(tile.getWorldX(), tile.getWorldY(), 32, 32);
                Rectangle rect = new Rectangle((int) x, (int) y, (int) width, (int) height);
                if (tileRect.intersects(rect)) {
                    int area = (int) (Math.min(tileRect.getWidth(), rect.getWidth()) * Math.min(tileRect.getHeight(), rect.getHeight()));
                    intersections.put(tile, area);
                }
            }
        }

        if (intersections.isEmpty()) {
            return Optional.empty();
        }

        Tile largestTile = null;
        int largestArea = 0;
        for (Map.Entry<Tile, Integer> entry : intersections.entrySet()) {
            if (entry.getValue() > largestArea) {
                largestArea = entry.getValue();
                largestTile = entry.getKey();
            }
        }

        return Optional.of(largestTile);
    }

    public Tile getTile(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException("Tile coordinates out of bounds");
        }
        return tiles[x][y];
    }

    public void onRender(Graphics2D graphics) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile tile = tiles[x][y];
                if (tile != null) {
                    tile.onRender(graphics);
                }
            }
        }
    }
}
