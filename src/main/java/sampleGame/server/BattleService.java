package sampleGame.server;

import common.IService;
import common.ServiceContext;
import sampleGame.data.BattleData;
import sampleGame.data.entity.Entity;
import sampleGame.data.entity.components.AnimationComponent;
import sampleGame.data.entity.components.PositionComponent;
import sampleGame.data.entity.components.SpriteComponent;
import sampleGame.data.entity.components.TileAnimationComponent;
import sampleGame.data.tile.Tile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

public class BattleService implements IService<BattleAction, Serializable> {
    private BattleData battleData;

    public BattleService() {
        var tiles = new Tile[16][16];
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[x].length; y++) {
                var tile = new Tile(x, y, "tiles/Cyan", true);
                tiles[x][y] = tile;
            }
        }
        battleData = new BattleData(tiles, defineEntities());
    }

    @Override
    public String getName() {
        return "BattleService";
    }

    @Override
    public Optional<Serializable> execute(ServiceContext context, BattleAction action) throws Exception {
        if (!(action instanceof BattleAction.GetState)) {
            throw new IllegalArgumentException("Unsupported action type: " + action.getClass().getSimpleName());
        }

        switch (action) {
            default -> {
                return Optional.of(getData());
            }
        }

    }

    private static ArrayList<Function<String, Entity>> defineEntities() {
        var entityFactories = new ArrayList<Function<String, Entity>>();
        Function<String, Entity> entityFactory = (id) -> {
            var entity = new Entity(id);
            entity.addComponent(new PositionComponent(4, 4));
            entity.addComponent(new SpriteComponent("sprites/test"));
            entity.addComponent(new AnimationComponent());
            entity.addComponent(new TileAnimationComponent());
            return entity;
        };
        entityFactories.add(entityFactory);
        return entityFactories;
    }

    // In BattleService
    public BattleData getData() {
        if (battleData == null) {
            throw new IllegalStateException("Battle data is not initialized.");
        }
        return battleData.deepCopy(); // Return a copy instead of the original
    }
}
