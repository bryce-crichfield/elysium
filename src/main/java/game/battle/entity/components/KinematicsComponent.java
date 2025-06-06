package game.battle.entity.components;

import game.battle.entity.Entity;
import game.battle.entity.component.UpdatableComponent;
import org.joml.Vector2f;

import java.time.Duration;

public class KinematicsComponent implements UpdatableComponent {
    private float accelerationX = 0;
    private float accelerationY = 0;
    private float velocityX = 0;
    private float velocityY = 0;

    public void direction(float x, float y) {
        this.accelerationX = x;
        this.accelerationY = y;
    }

    @Override
    public void onUpdate(Entity self, Duration delta) {
        if (self.lacksComponent(PositionComponent.class)) return;

        var position = self.getComponent(PositionComponent.class);

        float dt = delta.toMillis() / 1000.0f;

        // Update velocity based on acceleration
        velocityX += accelerationX * dt;
        velocityY += accelerationY * dt;

        // Apply friction (exponential decay)
        float frictionFactor = (float)Math.pow(1.0f - 0.9, dt);
        velocityX *= frictionFactor;
        velocityY *= frictionFactor;

        // Update position based on velocity
        position.setX(position.getX() + velocityX * dt);
        position.setY(position.getY() + velocityY * dt);
    }

    public Vector2f getVelocity() {
        return new Vector2f(velocityX, velocityY);
    }

    public void setVelocity(Vector2f velocity) {
        this.velocityX = velocity.x;
        this.velocityY = velocity.y;
    }
}
