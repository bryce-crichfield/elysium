package game.state.battle.entity;

import game.graphics.Renderer;
import game.graphics.texture.TextureStore;
import game.state.battle.entity.capabilities.HasSprite;
import game.state.battle.entity.components.*;
import game.state.battle.event.*;
import game.state.battle.util.Cursor;
import game.state.battle.world.Tile;
import game.state.character.GameCharacter;
import game.state.character.StarTrooper;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.time.Duration;
import java.util.List;

public class Entity implements HasSprite {
    @Getter
    PositionComponent position;
    @Getter
    SpriteComponent sprite;
    @Getter
    AnimationComponent animation;
    @Getter
    CharacterComponent character;
    @Getter
    VitalsComponent vitals;

    @Setter
    private boolean selected = false;
    private boolean hovered = false;
    private boolean isPlayer = true;

    @Getter
    @Setter
    private boolean waiting = false;

    public Entity(int x, int y) {
        this.position = new PositionComponent(x, y);
        animation = new AnimationComponent(position);
        vitals = new VitalsComponent();
        character = new CharacterComponent(StarTrooper.create());
        sprite = new SpriteComponent(position, TextureStore.getInstance().getAssets("sprites/test"));
    }

    public boolean isPlayer() {
        return isPlayer;
    }

    public void setPlayer(boolean player) {
        isPlayer = player;
    }

    public void move(List<Tile> movePath) {
        if (movePath.isEmpty()) return;

        // If the first tile in the path is the same as the current position, remove it
        if (movePath.get(0).getX() == position.getX() && movePath.get(0).getY() == position.getY()) {
            movePath.remove(0);
        }

        vitals.movementPoints -= movePath.size();
        animation.start(movePath);
    }

    public void onCursorMoved(Cursor cursor) {
        boolean cursorHovers = cursor.getCursorX() == this.position.getX() && cursor.getCursorY() == this.position.getY();

        if (hovered && !cursorHovers) {
            hovered = true;
            ActorHovered.event.fire(this);
        } else if (!hovered && cursorHovers) {
            hovered = false;
            ActorUnhovered.event.fire(this);
        }
    }

    public void onActorSelected(Entity entity) {
        if (entity.equals(this)) {
            selected = true;
        }
    }

    public void onActorDeselected(Entity entity) {
        if (entity.equals(this)) {
            selected = false;
        }
    }

    public void onActorAttacked(ActionActorAttack attack) {
        if (attack.getAttacker().equals(this)) {
            return;
        }

        for (Tile tile : attack.getTargets()) {
            if (tile.getX() == position.getX() && tile.getY() == position.getY()) {
                vitals.health -= attack.getAttacker().getAttack();
                ActorDamaged.event.fire(this);

                if (vitals.health <= 0) {
                    ActorKilled.event.fire(this);
                }
            }
        }
    }

    public float getAttack() {
        return 0;
//        return character.getStats().getPhysical();
    }

    public float getX() {
        return position.getX();
    }

    public float getY() {
        return position.getY();
    }

    public void onUpdate(Duration duration) {
        animation.onUpdate(duration);
        position.setX((int) animation.getX());
        position.setY((int) animation.getY());
    }

    public void onRender(Renderer renderer) {
        // Draw the actor
        float x = animation.getX();
        float y = animation.getY();

        // Draw the health bar
        float healthPercentage = vitals.health / character.getHealth();
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
