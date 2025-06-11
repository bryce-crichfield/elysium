package sampleGame.data.tile;

import client.core.graphics.Renderer;
import client.core.util.Util;
import client.runtime.application.Application;
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

    int tileSize = Application.TILE_SIZE;
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
      boolean hasAbove =
          tiles.stream().anyMatch(t -> t.getX() == tile.getX() && t.getY() == tile.getY() - 1);
      boolean hasBelow =
          tiles.stream().anyMatch(t -> t.getX() == tile.getX() && t.getY() == tile.getY() + 1);
      boolean hasLeft =
          tiles.stream().anyMatch(t -> t.getX() == tile.getX() - 1 && t.getY() == tile.getY());
      boolean hasRight =
          tiles.stream().anyMatch(t -> t.getX() == tile.getX() + 1 && t.getY() == tile.getY());

      renderer.setLineWidth(2);
      renderer.setColor(color);

      int tileX = tile.getX() * Application.TILE_SIZE;
      int tileY = tile.getY() * Application.TILE_SIZE;

      // Draw lines only where there are no neighbors
      if (!hasAbove) {
        renderer.drawLine(tileX, tileY, tileX + Application.TILE_SIZE, tileY);
      }

      if (!hasBelow) {
        renderer.drawLine(
            tileX,
            tileY + Application.TILE_SIZE,
            tileX + Application.TILE_SIZE,
            tileY + Application.TILE_SIZE);
      }

      if (!hasLeft) {
        renderer.drawLine(tileX, tileY, tileX, tileY + Application.TILE_SIZE);
      }

      if (!hasRight) {
        renderer.drawLine(
            tileX + Application.TILE_SIZE,
            tileY,
            tileX + Application.TILE_SIZE,
            tileY + Application.TILE_SIZE);
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
