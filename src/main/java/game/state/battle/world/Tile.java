package game.state.battle.world;
//
//import game.graphics.Renderer;
//import lombok.Getter;
//
//import java.awt.*;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//

import game.Game;
import game.graphics.Renderer;
import game.graphics.sprite.Sprite;
import game.graphics.sprite.SpriteRenderer;
import game.graphics.texture.Texture;
import game.graphics.texture.TextureStore;
import lombok.Getter;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Tile {
    @Getter
    private final int x;

    @Getter
    private final int y;
    private final String textureId;

    public Tile(int x, int y, String textureId) {
        this.x = x;
        this.y = y;
        this.textureId = textureId;
    }

    public static void drawOutline(List<Tile> tiles, Renderer renderer, Color color) {
        for (Tile tile : tiles) {
            List<Tile> neighbors = tile.getNeighbors(tiles);
            boolean hasAbove = neighbors.stream().anyMatch(neighbor -> neighbor.getY() < tile.getY());
            boolean hasBelow = neighbors.stream().anyMatch(neighbor -> neighbor.getY() > tile.getY());
            boolean hasLeft = neighbors.stream().anyMatch(neighbor -> neighbor.getX() < tile.getX());
            boolean hasRight = neighbors.stream().anyMatch(neighbor -> neighbor.getX() > tile.getX());

            var oldStroke = renderer.getLineWidth();
            renderer.setLineWidth(2);
            renderer.setColor(color);

            int tileX = tile.getX() * 32;
            int tileY = tile.getY() * 32;
            int tileWidth = 32;
            int tileHeight = 32;

            if (!hasAbove) {
                renderer.drawLine(tileX, tileY, tileX + tileWidth, tileY);
            }

            if (!hasBelow) {
                renderer.drawLine(tileX, tileY + tileHeight, tileX + tileWidth, tileY + tileHeight);
            }

            if (!hasLeft) {
                renderer.drawLine(tileX, tileY, tileX, tileY + tileHeight);
            }

            if (!hasRight) {
                renderer.drawLine(tileX + tileWidth, tileY, tileX + tileWidth, tileY + tileHeight);
            }

            renderer.setLineWidth(oldStroke);
        }
    }

    public static void drawTurtle(List<Tile> tiles, Renderer renderer, Color color) {
        if (tiles.isEmpty()) {
            return;
        }

        int tileSize = 32;
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

    public List<Tile> getNeighbors(List<Tile> tiles) {
        tiles = tiles.stream().filter(tile -> !tile.equals(this)).collect(Collectors.toList());
        // filter out if x and y are the same
        tiles = tiles.stream().filter(tile -> {
            return tile.getX() != this.getX() || tile.getY() != this.getY();
        }).collect(Collectors.toList());
        Optional<Tile> above = tiles.stream().filter(tile -> tile.getY() == getY() - 1).findFirst();
        Optional<Tile> below = tiles.stream().filter(tile -> tile.getY() == getY() + 1).findFirst();
        Optional<Tile> left = tiles.stream().filter(tile -> tile.getX() == getX() - 1).findFirst();
        Optional<Tile> right = tiles.stream().filter(tile -> tile.getX() == getX() + 1).findFirst();
        return Stream.of(above, below, left, right).filter(Optional::isPresent).map(Optional::get).collect(
                Collectors.toList());
    }

    public boolean isPassable() {
        return true;
    }


    public void onRender(SpriteRenderer renderer) {
        Texture texture = TextureStore.getInstance().getAssets("tiles/Cyan");
        Sprite sprite = new Sprite(texture, 0, 0, Game.TILE_SIZE, Game.TILE_SIZE);
        int drawX = (int) (getX() * Game.TILE_SIZE);
        int drawY = (int) (getY() * Game.TILE_SIZE);
        renderer.drawSprite(drawX, drawY, Game.TILE_SIZE, Game.TILE_SIZE, sprite);
//        var color = getTileComponent().getColor();
//        int size = Game.TILE_SIZE;
//        int alpha = (int) (255 * 0.15f);
//        var drawColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
//        renderer.setColor(drawColor);
//        renderer.fillRect((int) (getX() * size), (int) (getY() * size), size, size);
//        renderer.setColor(color.darker().darker());
//        renderer.drawRect((int) (getX() * size), (int) (getY() * size), size, size);
    }

}
