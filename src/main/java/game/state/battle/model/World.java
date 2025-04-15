package game.state.battle.model;

import game.graphics.Renderer;
import game.graphics.texture.SpriteRenderer;
import game.state.battle.model.capabilities.HasSprite;
import game.util.Util;
import lombok.Getter;

import java.awt.*;
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
    private final Tile[][] tiles;
    private final List<Actor> actors = new ArrayList<>();

    public World(int width, int height) {
        this.width = width;
        this.height = height;

        tiles = new Tile[width][height];

        // Initialize the tiles
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color color = Color.cyan;
                tiles[x][y] = new ColorTile(x, y, true, color);
            }
        }

        actors.add(new Actor(3, 0, Color.ORANGE));
        actors.add(new Actor(5, 0, Color.BLUE));

        Actor enemy = new Actor(4, 0, Color.GREEN);
        enemy.setPlayer(false);

        actors.add(enemy);
    }

    public void wall(int x, int y) {
        tiles[x][y] = new ColorTile(x, y, false, Color.RED.darker().darker().darker());
    }

    public void onUpdate(Duration duration) {
        for (Actor actor : actors) {
            actor.onUpdate(duration);
        }
    }

    public void onRender(Renderer renderer, SpriteRenderer spriteRenderer) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y].onRender(renderer);
            }
        }

        spriteRenderer.begin();
        for (Actor actor : actors) {
            if (actor instanceof HasSprite) {
                var spriteComponent = ((HasSprite) actor).getSpriteComponent();
                spriteComponent.onRender(spriteRenderer);
            }
        }
        spriteRenderer.end();

        for (Actor actor : actors) {
            actor.onRender(renderer);
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

    public Optional<Actor> getActorByPosition(int x, int y) {
        for (Actor actor : actors) {
            if (actor.getX() == x && actor.getY() == y) {
                return Optional.of(actor);
            }
        }

        return Optional.empty();
    }

    public void addActor(Actor actor) {
        actors.add(actor);
    }

    public void removeActor(Actor actor) {
        actors.remove(actor);
    }

    public List<Actor> getActors() {
        return actors;
    }

    public Optional<Actor> findActor(Predicate<Actor> predicate) {
        for (Actor actor : actors) {
            if (predicate.test(actor)) {
                return Optional.of(actor);
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
