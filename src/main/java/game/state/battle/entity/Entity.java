package game.state.battle.entity;

import game.graphics.Renderer;
import game.graphics.sprite.SpriteRenderer;
import game.state.battle.entity.component.Component;
import game.state.battle.entity.component.RenderableComponent;
import game.state.battle.entity.component.UpdatableComponent;

import java.io.*;
import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class Entity implements Serializable {
    private final Map<Class<? extends Component>, Component> components = new HashMap<>();

    public Entity() {
    }


    public static void serialize(Collection<Entity> entities, String path) {
        try (FileOutputStream fileOut = new FileOutputStream(path);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            // Write the entire collection at once
            out.writeObject(entities);
            System.out.println("Entities serialized to " + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Collection<Entity> deserialize(String path) {
        Collection<Entity> entities = null;
        try (FileInputStream fileIn = new FileInputStream(path);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            // Read and cast the collection
            entities = (Collection<Entity>) in.readObject();
            System.out.println("Entities deserialized from " + path);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return entities;
    }

    public void onUpdate(Duration duration) {
        for (Component component : this.components.values()) {
            if (component instanceof UpdatableComponent updatable) {
                updatable.onUpdate(this, duration);
            }
        }
    }

    public void onSpriteRender(SpriteRenderer spriteRenderer) {
        for (Component component : this.components.values()) {
            if (component instanceof RenderableComponent renderable) {
                renderable.onSpriteRender(this, spriteRenderer);
            }
        }
    }

    public void onVectorRender(Renderer renderer) {
        for (Component component : this.components.values()) {
            if (component instanceof RenderableComponent renderable) {
                renderable.onVectorRender(this, renderer);
            }
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

    public <T extends Component> boolean lacksComponent(Class<T> componentClass) {
        return !components.containsKey(componentClass);
    }

    public Map<Class<? extends Component>, Component> getComponents() {
        return components;
    }
}
