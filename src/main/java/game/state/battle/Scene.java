package game.state.battle;

import game.graphics.Renderer;
import game.graphics.sprite.SpriteRenderer;
import game.state.battle.entity.Entity;
import game.state.battle.entity.components.PositionComponent;
import game.state.battle.tile.Tile;
import game.state.battle.tile.TileArea;
import lombok.Getter;

import java.io.*;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class Scene implements Serializable {
    private final int width;
    private final int height;
    private final Tile[][] tiles;
    private final List<Entity> entities;

    public Scene(Tile[][] tiles, List<Entity> entities) {
        this.tiles = tiles;
        this.entities = entities;
        this.width = tiles.length;
        this.height = tiles[0].length;
    }

    public static void serialize(String path, Scene scene) throws RuntimeException {
        try (var fileOut = new FileOutputStream(path);
             var out = new ObjectOutputStream(fileOut)) {
            out.writeObject(scene);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Scene deserialize(String path) throws RuntimeException {
        try (FileInputStream fileIn = new FileInputStream(path);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            return (Scene) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void onUpdate(Duration duration) {
        for (Entity entity : entities) {
            entity.onUpdate(duration);
        }
    }

    public void onRender(Renderer renderer, SpriteRenderer spriteRenderer) {
        spriteRenderer.begin();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y].onSpriteRender(spriteRenderer);
            }
        }

        spriteRenderer.end();

        spriteRenderer.begin();
        for (Entity entity : entities) {
            entity.onSpriteRender(spriteRenderer);
        }
        spriteRenderer.end();

        for (Entity entity : entities) {
            entity.onVectorRender(renderer);
        }
    }

    public Tile getTile(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return null;
        }
        return tiles[x][y];
    }


    public Optional<Entity> findEntityByPosition(int x, int y) {
        for (Entity entity : entities) {
            if (entity.lacksComponent(PositionComponent.class)) continue;
            var position = entity.getComponent(PositionComponent.class);
            if (position.getX() == x && position.getY() == y) {
                return Optional.of(entity);
            }
        }

        return Optional.empty();
    }

    public void addActor(Entity entity) {
        entities.add(entity);
    }

    public void removeActor(Entity entity) {
        entities.remove(entity);
    }

    public List<Entity> getEntities() {
        return entities.stream().toList();
    }


    public TileArea getTiles() {
        var tileList = Arrays.stream(tiles)
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());
        return new TileArea(tileList);
    }
}
