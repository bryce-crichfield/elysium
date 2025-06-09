package sampleGame.data.entity.entities;

import client.core.graphics.Renderer;
import sampleGame.data.entity.Entity;
import sampleGame.data.entity.component.Component;
import sampleGame.data.entity.component.RenderableComponent;
import sampleGame.data.entity.component.UpdatableComponent;
import sampleGame.data.entity.components.KinematicsComponent;
import sampleGame.data.entity.components.PositionComponent;

import java.awt.*;
import java.time.Duration;
import java.util.UUID;

public class Fireball {

    public static Entity create(int x, int y) {
        var entity = new Entity(UUID.randomUUID().toString());

        entity.addComponent(new PositionComponent(x, y));

        var kinematics = new KinematicsComponent();
        kinematics.direction(0, 10);
        entity.addComponent(kinematics);
        entity.addComponent(new FireballRenderable());
        entity.addComponent(new FireballUpdatable());

        return entity;
    }

    private static class FireballUpdatable implements UpdatableComponent {
        Duration aliveTime = Duration.ofSeconds(3);

        @Override
        public void onUpdate(Entity self, Duration delta) {
            aliveTime = aliveTime.minus(delta);
            if (aliveTime.isZero() || aliveTime.isNegative()) {
                self.setDead(true);
            }
        }

        @Override
        public Component clone() {
            return new FireballUpdatable();
        }
    }

    private static class FireballRenderable implements RenderableComponent {
        @Override
        public void onVectorRender(Entity self, Renderer renderer) {
            RenderableComponent.super.onVectorRender(self, renderer);

            if (self.lacksComponent(PositionComponent.class)) return;

            var position = self.getComponent(PositionComponent.class);

            // Draw the fireball at its position
            renderer.setColor(Color.RED);
            var drawX = position.getX() * 32;
            var drawY = position.getY() * 32;
            renderer.fillOval((int) drawX + 8, (int) drawY + 8, 16, 16);
        }

        @Override
        public Component clone() {
    return new FireballRenderable();
        }
    }
}
