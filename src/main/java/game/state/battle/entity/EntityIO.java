package game.state.battle.entity;

import com.google.gson.*;
import game.state.battle.entity.component.ComponentDeserializerRegistry;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EntityIO {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    static {
        ComponentDeserializerRegistry.registerDeserializers();
    }

    public static void saveScene(String filePath, List<Entity> entities, List<Entity> world) throws IOException {
        JsonObject root = new JsonObject();

        // Serialize entities
        JsonArray entitiesJson = new JsonArray();
        for (Entity entity : entities) {
            entitiesJson.add(entity.serialize());
        }
        root.add("entities", entitiesJson);

        // Add world data
        JsonArray worldJson = new JsonArray();
        for (Entity entity : world) {
            worldJson.add(entity.serialize());
        }
        root.add("world", worldJson);

        // Write to file
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(root, writer);
        }
    }

    public static BattleScene loadScene(String filePath) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            // Deserialize entities
            List<Entity> entities = new ArrayList<>();
            JsonArray entitiesJson = root.getAsJsonArray("entities");
            for (JsonElement entityElement : entitiesJson) {
                Entity entity = Entity.deserialize(entityElement.getAsJsonObject());
                entities.add(entity);
            }

            // Get world data
            List<Entity> worldData = new ArrayList<>();
            JsonArray worldJson = root.getAsJsonArray("world");
            for (JsonElement entityElement : worldJson) {
                Entity entity = Entity.deserialize(entityElement.getAsJsonObject());
                worldData.add(entity);
            }

            return new BattleScene(entities, worldData);
        }
    }

    public static class BattleScene {
        private final List<Entity> entities;
        private final List<Entity> worldData;

        public BattleScene(List<Entity> entities, List<Entity> worldData) {
            this.entities = entities;
            this.worldData = worldData;
        }

        public List<Entity> getEntities() {
            return entities;
        }

        public List<Entity> getWorldData() {
            return worldData;
        }
    }
}
