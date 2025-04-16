package game.state.battle;

import game.graphics.Renderer;
import game.graphics.sprite.SpriteRenderer;
import game.state.battle.entity.Entity;
import game.state.battle.util.Raycast;
import game.util.Util;
import lombok.Getter;

import java.io.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Getter
public class Scene implements Serializable {
    private final int width;
    private final int height;
    private final Tile[][] tiles;
    private final List<Entity> entities;

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

    public Scene(Tile[][] tiles, List<Entity> entities) {
        this.tiles = tiles;
        this.entities = entities;
        this.width = tiles.length;
        this.height = tiles[0].length;
    }

//    public Scene(int width, int height) {
//        this.width = width;
//        this.height = height;
//
////        tiles = new Tile[width][height];
//
//        // Initialize the tiles
//        tiles = new Tile[width][height];
//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                var tile = new Tile(x, y, "");
//                tiles[x][y] = tile;
//            }
//        }
////
////        entities = new ArrayList<>();
////        var entity = new Entity();
////        var position = new PositionComponent(0, 0);
////        entity.addComponent(position);
////        entity.addComponent(new SpriteComponent("sprites/test"));
////
////        entities.add(entity);
////
////        Entity.serialize(entities, "test.bin");
//        entities = Entity.deserialize("test.bin");
//    }

    public void onUpdate(Duration duration) {
        for (Entity entity : entities) {
            entity.onUpdate(duration);
        }
    }

    public void onRender(Renderer renderer, SpriteRenderer spriteRenderer) {
        spriteRenderer.begin();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y].onRender(spriteRenderer);
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

    public Tile[] getNeighbors(int x, int y) {
        List<Tile> neighbors = new ArrayList<>();

        // Up
        if (y > 0) {
            neighbors.add(tiles[x][y - 1]);
        }

        // Down
        if (y < height - 1) {
            neighbors.add(tiles[x][y + 1]);
        }

        // Left
        if (x > 0) {
            neighbors.add(tiles[x - 1][y]);
        }

        // Right
        if (x < width - 1) {
            neighbors.add(tiles[x + 1][y]);
        }

        return neighbors.toArray(new Tile[0]);
    }

    public Optional<Entity> getActorByPosition(int x, int y) {
        for (Entity entity : entities) {
//            if (entity.getX() == x && entity.getY() == y) {
//                return Optional.of(entity);
//            }
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

    public Optional<Entity> findActor(Predicate<Entity> predicate) {
        for (Entity entity : entities) {
            if (predicate.test(entity)) {
                return Optional.of(entity);
            }
        }

        return Optional.empty();
    }

    public Raycast raycast(int startX, int startY, int endX, int endY) {
        return new Raycast(tiles, startX, startY, endX, endY);
    }

    public List<Tile> getTilesInRange(int x, int y, int walkDistance) {
        List<Tile> found = new ArrayList<>();

        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                if (Util.distance(x, y, tile.getX(), tile.getY()) <= walkDistance) {
                    found.add(tile);
                }
            }
        }

        return found;
    }
}
