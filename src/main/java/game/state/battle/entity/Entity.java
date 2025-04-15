package game.state.battle.entity;

import com.google.gson.JsonObject;
import game.graphics.Renderer;
import game.state.battle.entity.component.ComponentRegistry;
import game.state.battle.entity.component.JsonSerializable;
import game.state.battle.entity.components.*;
import game.state.battle.entity.component.Component;
import game.state.battle.event.*;
import game.state.battle.world.Tile;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entity {
    private final String id;
    private Map<Class<? extends Component>, Component> components = new HashMap<>();

    @Getter
    @Setter
    private boolean waiting = false;

    public Entity(String id) {
        this.id = id;
//        this.position = new PositionComponent(x, y);
//        animation = new AnimationComponent(position);
//        vitals = new VitalsComponent();
//        character = new CharacterComponent(StarTrooper.create());
//        sprite = new SpriteComponent(position, TextureStore.getInstance().getAssets("sprites/test"));
    }

    public void move(List<Tile> movePath) {
        if (movePath.isEmpty()) return;

        // If the first tile in the path is the same as the current position, remove it
        var position = getComponent(PositionComponent.class);
//        var vitals = getComponent(VitalsComponent.class);
        var animation = getComponent(AnimationComponent.class);

        if (movePath.get(0).getX() == position.getX() && movePath.get(0).getY() == position.getY()) {
            movePath.remove(0);
        }

//        vitals.movementPoints -= movePath.size();
        animation.start(movePath);
    }

//    public void onCursorMoved(Cursor cursor) {
//        boolean cursorHovers = cursor.getCursorX() == this.position.getX() && cursor.getCursorY() == this.position.getY();
//
//        if (hovered && !cursorHovers) {
//            hovered = true;
//            ActorHovered.event.fire(this);
//        } else if (!hovered && cursorHovers) {
//            hovered = false;
//            ActorUnhovered.event.fire(this);
//        }
//    }


//    public void onActorAttacked(ActionActorAttack attack) {
//        if (attack.getAttacker().equals(this)) {
//            return;
//        }
//
//        for (Tile tile : attack.getTargets()) {
//            if (tile.getX() == position.getX() && tile.getY() == position.getY()) {
//                vitals.health -= attack.getAttacker().getAttack();
//                ActorDamaged.event.fire(this);
//
//                if (vitals.health <= 0) {
//                    ActorKilled.event.fire(this);
//                }
//            }
//        }
//    }

    public float getX() {
        return getComponent(PositionComponent.class).getX();
    }

    public float getY() {
        return getComponent(PositionComponent.class).getY();
    }

    public void onUpdate(Duration duration) {
        var animation = getComponent(AnimationComponent.class);
        var position = getComponent(PositionComponent.class);

        animation.onUpdate(duration);
        position.setX((int) animation.getX());
        position.setY((int) animation.getY());
    }

    public void onRender(Renderer renderer) {
        if (hasComponent(AnimationComponent.class)) {
            var animation = getComponent(AnimationComponent.class);

            // Draw the actor
            float x = animation.getX();
            float y = animation.getY();

            // Draw the health bar
//        float healthPercentage = vitals.health / character.getHealth();
            float healthPercentage = 1;
            Color healthColor = healthPercentage > 0.5 ? Color.GREEN : healthPercentage > 0.25 ? Color.YELLOW : Color.RED;
            renderer.setColor(healthColor);
            int healthWidth = (int) ((32 - 10) * healthPercentage);
            int healthHeight = 5;
            int healthX = (int) ((x * 32) + 5);
            int healthY = (int) ((y * 32) + 32 - 5);

            renderer.setColor(Color.BLACK);
            renderer.fillRect(healthX, healthY, 32 - 10, healthHeight);
            renderer.setColor(healthColor);
            renderer.fillRect(healthX, healthY, healthWidth, healthHeight);
            renderer.setColor(Color.BLACK);
            renderer.drawRect(healthX, healthY, 32 - 10, healthHeight);
        }
    }

    public <T extends Component> void addComponent(T component) {
        components.put(component.getClass(), component);
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        return componentClass.cast(components.get(componentClass));
    }

    public <T extends Component> boolean hasComponent(Class<T> componentClass) {
        return components.containsKey(componentClass);
    }

    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("id", id);

        JsonObject componentsJson = new JsonObject();
        for (var component : components.values()) {
            if (component instanceof JsonSerializable serializable) {
                var type = component.getComponentType();
                var jsonComponent = serializable.jsonSerialize();
                componentsJson.add(type, jsonComponent);
            }
        }
        json.add("components", componentsJson);

        return json;
    }

    public static Entity deserialize(JsonObject json) {
        String id = json.get("id").getAsString();
        Entity entity = new Entity(id);

        JsonObject componentsJson = json.getAsJsonObject("components");
        for (String componentType : componentsJson.keySet()) {
            JsonObject componentJson = componentsJson.getAsJsonObject(componentType);
            Component component = ComponentRegistry.deserialize(componentType, componentJson);
            entity.addComponent(component);
        }

        return entity;
    }
}
