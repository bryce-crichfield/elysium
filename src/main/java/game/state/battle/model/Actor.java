package game.state.battle.model;

import game.state.character.GameCharacter;
import game.state.character.StarTrooper;
import game.graphics.Renderer;
import game.graphics.texture.TextureStore;
import game.state.battle.event.*;
import game.state.battle.model.capabilities.HasSprite;
import game.state.battle.model.components.PositionComponent;
import game.state.battle.model.components.SpriteComponent;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.time.Duration;
import java.util.List;

public class Actor implements HasSprite {
    PositionComponent position;

    @Getter
    SpriteComponent spriteComponent ;

    ActorAnimation animation;
    GameCharacter character = StarTrooper.create();
    float currentHealthPoints = character.getStats().getHealth();
    float currentMovementPoints = character.getStats().getSpeed();
    Color color;

    @Setter
    private boolean selected = false;
    private boolean hovered = false;
    private boolean isPlayer = true;
    @Getter
    @Setter
    private boolean waiting = false;

    public Actor(int x, int y, Color color) {
        this.position = new PositionComponent(x, y);
        this.color = color;

        animation = new ActorAnimation(this);

        spriteComponent = new SpriteComponent(position, TextureStore.getInstance().getAssets("sprites/test"));
    }

    public boolean isPlayer() {
        return isPlayer;
    }

    public void setPlayer(boolean player) {
        isPlayer = player;
    }

    public int getAttackDistance() {
        return character.getStats().getRange();
    }

    public String getName() {
        return character.getName();
    }

    public float getHealth() {
        return currentHealthPoints;
    }

    public void move(List<Tile> movePath) {
        if (movePath.isEmpty()) return;

        // If the first tile in the path is the same as the current position, remove it
        if (movePath.get(0).getX() == position.getX() && movePath.get(0).getY() == position.getY()) {
            movePath.remove(0);
        }

        currentMovementPoints -= movePath.size();
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

//    public void onActorSelected(Actor actor) {
//        if (actor.equals(this)) {
//            selected = true;
//        }
//    }

    public void onActorDeselected(Actor actor) {
        if (actor.equals(this)) {
            selected = false;
        }
    }

    public void onActorAttacked(ActionActorAttack attack) {
        if (attack.getAttacker().equals(this)) {
            return;
        }

        for (Tile tile : attack.getTargets()) {
            if (tile.getX() == position.getX() && tile.getY() == position.getY()) {
                currentHealthPoints -= attack.getAttacker().getAttack();
                ActorDamaged.event.fire(this);

                if (currentHealthPoints <= 0) {
                    ActorKilled.event.fire(this);
                }
            }
        }
    }

    public float getAttack() {
        return character.getStats().getPhysical();
    }

    public float getX() {
        return position.getX();
    }

    public float getY() {
        return position.getY();
    }

    public void onUpdate(Duration duration) {
        animation.onUpdate(duration);
        position.setX((int)animation.getX());
        position.setY((int)animation.getY());
    }

    public void onRender(Renderer renderer) {
        // Draw the actor
        Color color = selected ? Color.GREEN : waiting ? Color.GRAY : this.color;
        color = !isPlayer ? Color.RED : color;
        float x = animation.getX();
        float y = animation.getY();
//        renderer.setColor(color);
//        renderer.fillOval((int) (x * 32), (int) (y * 32), 32, 32);
//        renderer.setColor(Color.BLACK);
//        renderer.drawOval((int) (x * 32), (int) (y * 32), 32, 32);

        // Draw the health bar
        float healthPercentage = currentHealthPoints / character.getStats().getHealth();
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

    public int getMovementPoints() {
        return (int) currentMovementPoints;
    }
}
