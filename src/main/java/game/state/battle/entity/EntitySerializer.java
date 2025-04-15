package game.state.battle.entity;

import com.google.gson.*;
import game.state.battle.entity.component.ComponentRegistry;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EntitySerializer {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    static {
        ComponentRegistry.init();
    }

    public static void saveScene(String filePath, List<Entity> entities, JsonObject worldData) throws IOException {
        JsonObject root = new JsonObject();

        // Serialize entities
        JsonArray entitiesJson = new JsonArray();
        for (Entity entity : entities) {
            entitiesJson.add(entity.serialize());
        }
        root.add("entities", entitiesJson);

        // Add world data
        root.add("world", worldData);

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
            JsonObject worldData = root.getAsJsonObject("world");

            return new BattleScene(entities, worldData);
        }
    }

    public static class BattleScene {
        private final List<Entity> entities;
        private final JsonObject worldData;

        public BattleScene(List<Entity> entities, JsonObject worldData) {
            this.entities = entities;
            this.worldData = worldData;
        }

        public List<Entity> getEntities() {
            return entities;
        }

        public JsonObject getWorldData() {
            return worldData;
        }
    }
}
