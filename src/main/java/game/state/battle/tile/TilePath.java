package game.state.battle.tile;

import game.Game;
import game.graphics.Renderer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.awt.*;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class TilePath {
    private final List<Tile> tiles;

    public TilePath() {
        this.tiles = List.of();
    }

    public void drawPath(Renderer renderer, Color color) {
        if (tiles.isEmpty()) {
            return;
        }

        int tileSize = Game.TILE_SIZE;
        // Draw the path
        var stroke = renderer.getLineWidth();
        renderer.setColor(color);
        renderer.setLineWidth(4);

        Tile start = tiles.get(0);

        float turtleTileX = start.getX();
        float turtleTileY = start.getY();

        for (Tile tile : tiles) {
            int tileX = tile.getX();
            int tileY = tile.getY();

            boolean isVertical = turtleTileX == tileX;
            boolean isHorizontal = turtleTileY == tileY;

            int turtleX = (int) (turtleTileX * tileSize);
            int turtleY = (int) (turtleTileY * tileSize);

            if (isVertical) {
                int centerX = turtleX + (tileSize / 2);
                int startY = turtleY + (tileSize / 2);
                int endY = (tileY * tileSize) + (tileSize / 2);

                renderer.drawLine(centerX, startY, centerX, endY);
            }

            if (isHorizontal) {
                int centerY = turtleY + (tileSize / 2);
                int startX = turtleX + (tileSize / 2);
                int endX = (tileX * tileSize) + (tileSize / 2);

                renderer.drawLine(startX, centerY, endX, centerY);
            }

            turtleTileX = tileX;
            turtleTileY = tileY;
        }
        renderer.setLineWidth(stroke);
    }

    public boolean isEmpty() {
        return tiles.isEmpty();
    }

    public void clear() {
        tiles.clear();
    }
}
