package game.battle.tile;

import core.GameContext;
import core.graphics.Renderer;
import core.util.Util;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TileArea {
    private final List<Tile> tiles;

    public TileArea(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public void fillArea(Renderer renderer, Color color) {
        if (tiles.isEmpty()) {
            return;
        }

        int tileSize = GameContext.TILE_SIZE;
        renderer.setColor(color);

        for (Tile tile : tiles) {
            int tileX = tile.getX() * tileSize;
            int tileY = tile.getY() * tileSize;

            renderer.fillRect(tileX, tileY, tileSize, tileSize);
        }
    }

    public void drawOutline(Renderer renderer, Color color) {
        for (Tile tile : tiles) {
            // Check for specific neighbors at exact positions
            boolean hasAbove = tiles.stream().anyMatch(t -> t.getX() == tile.getX() && t.getY() == tile.getY() - 1);
            boolean hasBelow = tiles.stream().anyMatch(t -> t.getX() == tile.getX() && t.getY() == tile.getY() + 1);
            boolean hasLeft = tiles.stream().anyMatch(t -> t.getX() == tile.getX() - 1 && t.getY() == tile.getY());
            boolean hasRight = tiles.stream().anyMatch(t -> t.getX() == tile.getX() + 1 && t.getY() == tile.getY());

            renderer.setLineWidth(2);
            renderer.setColor(color);

            int tileX = tile.getX() * GameContext.TILE_SIZE;
            int tileY = tile.getY() * GameContext.TILE_SIZE;

            // Draw lines only where there are no neighbors
            if (!hasAbove) {
                renderer.drawLine(tileX, tileY, tileX + GameContext.TILE_SIZE, tileY);
            }

            if (!hasBelow) {
                renderer.drawLine(tileX, tileY + GameContext.TILE_SIZE, tileX + GameContext.TILE_SIZE, tileY + GameContext.TILE_SIZE);
            }

            if (!hasLeft) {
                renderer.drawLine(tileX, tileY, tileX, tileY + GameContext.TILE_SIZE);
            }

            if (!hasRight) {
                renderer.drawLine(tileX + GameContext.TILE_SIZE, tileY, tileX + GameContext.TILE_SIZE, tileY + GameContext.TILE_SIZE);
            }
        }
    }

    public TileArea within(int x, int y, int distance) {
        List<Tile> found = new ArrayList<>();

        for (Tile tile : tiles) {
            var tileDistance = Util.distance(x, y, tile.getX(), tile.getY());
            if (tileDistance <= distance) {
                found.add(tile);
            }
        }

        return new TileArea(found);
    }

    public List<Tile> toList() {
        return new ArrayList<>(tiles);
    }
}
