package game.state.battle.entity.component;

import com.google.gson.JsonObject;
import game.state.battle.entity.components.PositionComponent;
import game.state.battle.entity.components.VitalsComponent;

import java.util.HashMap;
import java.util.Map;

public class ComponentRegistry {
    private static final Map<String, ComponentDeserializer<?>> deserializers = new HashMap<>();

    /**
     * Register a component deserializer
     * @param deserializer The deserializer to register
     */
    public static void registerDeserializer(ComponentDeserializer<?> deserializer) {
        deserializers.put(deserializer.getComponentType(), deserializer);
    }

    /**
     * Deserialize a component from JSON
     * @param type Component type
     * @param json JSON representation
     * @return Deserialized component
     */
    public static Component deserialize(String type, JsonObject json) {
        ComponentDeserializer<?> deserializer = deserializers.get(type);
        if (deserializer == null) {
            throw new IllegalArgumentException("No deserializer registered for component type: " + type);
        }
        return deserializer.deserialize(json);
    }

    public static void init() {
        // Register all your component deserializers here
        registerDeserializer(new PositionComponent.Deserializer());
//        registerDeserializer(new VitalsComponent.Deserializer());
        // Add other deserializers
    }
}
