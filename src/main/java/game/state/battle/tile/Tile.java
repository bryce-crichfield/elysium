package game.state.battle.tile;

import game.Game;
import game.graphics.sprite.Sprite;
import game.graphics.sprite.SpriteRenderer;
import game.graphics.texture.Texture;
import game.graphics.texture.TextureStore;
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
        Sprite sprite = new Sprite(texture, 0, 0, Game.TILE_SIZE, Game.TILE_SIZE);
        int drawX = getX() * Game.TILE_SIZE;
        int drawY = getY() * Game.TILE_SIZE;
        renderer.drawSprite(drawX, drawY, Game.TILE_SIZE, Game.TILE_SIZE, sprite);
    }
}
