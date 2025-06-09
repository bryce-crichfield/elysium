package sampleGame.data.tile;

import client.runtime.application.Application;
import client.core.graphics.sprite.Sprite;
import client.core.graphics.sprite.SpriteRenderer;
import client.core.graphics.texture.Texture;
import client.core.graphics.texture.TextureStore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public class Tile implements Serializable {
    private final int x;
    private final int y;
    private final String textureId;
    private final boolean isPassable;

    public TileArea getNeighbors(TileArea area) {
        var tiles = area.toList();
        Optional<Tile> above = tiles.stream()
                .filter(tile -> tile.getX() == getX() && tile.getY() == getY() - 1)
                .findFirst();
        Optional<Tile> below = tiles.stream()
                .filter(tile -> tile.getX() == getX() && tile.getY() == getY() + 1)
                .findFirst();
        Optional<Tile> left = tiles.stream()
                .filter(tile -> tile.getX() == getX() - 1 && tile.getY() == getY())
                .findFirst();
        Optional<Tile> right = tiles.stream()
                .filter(tile -> tile.getX() == getX() + 1 && tile.getY() == getY())
                .findFirst();

        var neighbors = Stream.of(above, below, left, right)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        return new TileArea(neighbors);
    }

    public void onSpriteRender(SpriteRenderer renderer) {
        Texture texture = TextureStore.getInstance().getAssets("tiles/Cyan");
        Sprite sprite = new Sprite(texture, 0, 0, Application.TILE_SIZE, Application.TILE_SIZE);
        int drawX = getX() * Application.TILE_SIZE;
        int drawY = getY() * Application.TILE_SIZE;
        renderer.drawSprite(drawX, drawY, Application.TILE_SIZE, Application.TILE_SIZE, sprite);
    }

    public Tile deepCopy() {
        return new Tile(x, y, textureId, isPassable);
    }
}
