package sampleGame.data;

import client.core.graphics.Renderer;
import client.core.graphics.sprite.SpriteRenderer;
import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import sampleGame.data.entity.Entity;
import sampleGame.data.entity.components.PositionComponent;
import sampleGame.data.tile.Tile;
import sampleGame.data.tile.TileArea;

@Getter
public class BattleData implements Serializable {
  private final int width;
  private final int height;
  private final Tile[][] tiles;
  private final Map<String, Entity> entities = new HashMap<>();

  public BattleData(Tile[][] tiles, List<Function<String, Entity>> entityFactories) {
    this.tiles = tiles;

    for (var factory : entityFactories) {
      // default entity id is a random UUID
      var entityId = UUID.randomUUID().toString();
      Entity entity = factory.apply(entityId);
      entities.put(entityId, entity);
    }

    this.width = tiles.length;
    this.height = tiles[0].length;
  }

  public BattleData(int width, int height, Tile[][] tiles, Map<String, Entity> entities) {
    this.tiles = tiles;
    this.width = width;
    this.height = height;
    this.entities.putAll(entities); // add all entities to the map
  }

  public static void serialize(String path, BattleData scene) throws RuntimeException {
    try (var fileOut = new FileOutputStream(path);
        var out = new ObjectOutputStream(fileOut)) {
      out.writeObject(scene);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static byte[] serializeToBytes(BattleData scene) throws RuntimeException {
    try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut)) {
      out.writeObject(scene);
      return byteOut.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static BattleData deserialize(String path) throws RuntimeException {
    try (FileInputStream fileIn = new FileInputStream(path);
        ObjectInputStream in = new ObjectInputStream(fileIn)) {
      return (BattleData) in.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public void onUpdate(Duration duration) {
    for (var entity : entities.entrySet()) {
      entity.getValue().onUpdate(duration);
    }

    for (var entry : entities.entrySet()) {
      if (entry.getValue().isDead()) {
        entities.remove(entry.getKey());
      }
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
    for (var entity : entities.entrySet()) {
      entity.getValue().onSpriteRender(spriteRenderer);
    }
    //        for (Entity entity : entities) {
    //            entity.onSpriteRender(spriteRenderer);
    //        }
    spriteRenderer.end();

    //        for (Entity entity : entities) {
    //            entity.onVectorRender(renderer);
    //        }
    for (var entry : entities.entrySet()) {
      entry.getValue().onVectorRender(renderer);
    }
  }

  public Tile getTile(int x, int y) {
    if (x < 0 || x >= width || y < 0 || y >= height) {
      return null;
    }
    return tiles[x][y];
  }

  public Optional<Entity> findEntityByPosition(int x, int y) {
    for (var entry : entities.entrySet()) {
      var entity = entry.getValue();
      if (entity.lacksComponent(PositionComponent.class)) continue;
      var position = entity.getComponent(PositionComponent.class);
      if (position.getX() == x && position.getY() == y) {
        return Optional.of(entity);
      }
    }

    return Optional.empty();
  }

  public void addEntity(Entity entity) {
    //        entities.add(entity);
    if (entity.getId() == null || entities.containsKey(entity.getId())) {
      throw new IllegalArgumentException("Entity must have a unique ID");
    }
    entities.put(entity.getId(), entity);
  }

  public void removeEntity(Entity entity) {
    entities.remove(entity);
  }

  public List<Entity> getEntities() {
    //        return entities.stream().toList();
    return entities.values().stream()
        .filter(e -> !e.isDead()) // filter out dead entities
        .collect(Collectors.toList());
  }

  public TileArea getTiles() {
    var tileList = Arrays.stream(tiles).flatMap(Arrays::stream).collect(Collectors.toList());
    return new TileArea(tileList);
  }

  public BattleData deepCopy() {
    Tile[][] copiedTiles = new Tile[width][height];
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        copiedTiles[x][y] = tiles[x][y].deepCopy(); // Assuming Tile has a deepCopy method
      }
    }

    Map<String, Entity> copiedEntities = new HashMap<>();
    for (var entry : entities.entrySet()) {
      Entity originalEntity = entry.getValue();
      Entity copiedEntity = originalEntity.deepCopy(); // Assuming Entity has a deepCopy method
      copiedEntities.put(copiedEntity.getId(), copiedEntity);
    }

    return new BattleData(
        width,
        height,
        copiedTiles,
        copiedEntities); // Return a new BattleData with copied tiles and entities
  }
}
