package game.state.battle.world;

import game.graphics.Renderer;
import game.graphics.sprite.SpriteRenderer;
import game.graphics.texture.TextureStore;
import game.state.battle.entity.Entity;
import game.state.battle.entity.EntityIO;
import game.state.battle.entity.components.PositionComponent;
import game.state.battle.entity.components.SpriteComponent;
import game.state.battle.util.Raycast;
import game.util.Util;
import lombok.Getter;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class World {
    @Getter
    private final int width;
    @Getter
    private final int height;
//    private final Tile[][] tiles;

    private Tile[][] tiles;
    private List<Entity> entities;

    public World(int width, int height) {
        this.width = width;
        this.height = height;

//        tiles = new Tile[width][height];

        // Initialize the tiles
        tiles = new Tile[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                var tile = new Tile(x, y, "");
                tiles[x][y] = tile;
            }
        }
//
        entities = new ArrayList<>();
        var entity = new Entity("test");
        var position = new PositionComponent(0, 0);
        entity.addComponent(position);
        var texture = TextureStore.getInstance().getAssets("sprites/test");
        entity.addComponent(new SpriteComponent(position, texture));

        entities.add(entity);

//        try {
//            var tileEntities = new ArrayList<Entity>();
//            for (int x = 0; x < width; x++) {
//                for (int y = 0; y < height; y++) {
//                    var tile = tiles[x][y];
//                    tileEntities.add(tile.getEntity());
//                }
//            }
//            EntitySerializer.saveScene("test.json", entities, tileEntities);
//            var scene = EntityIO.loadScene("test.json");
//            this.entities = scene.getEntities();
//            this.tiles = new Tile[width][height];
//            var loadedWorld = scene.getWorldData();
//            for (int x = 0; x < width; x++) {
//                for (int y = 0; y < height; y++) {
//                    var tile = loadedWorld.get(x * width + y);
//                    if (tile != null) {
//                        tiles[x][y] = new Tile(x, y, "");
//                    }
//                }
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

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
                tiles[x][y].onRender(spriteRenderer);
            }
        }

        spriteRenderer.end();

        spriteRenderer.begin();
        for (Entity entity : entities) {
            if (entity.hasComponent(SpriteComponent.class)) {
                var spriteComponent = entity.getComponent(SpriteComponent.class);
                spriteComponent.onRender(spriteRenderer);
            }
        }
        spriteRenderer.end();

        for (Entity entity : entities) {
            entity.onRender(renderer);
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
            if (entity.getX() == x && entity.getY() == y) {
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
        return entities;
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
