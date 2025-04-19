package game.state.battle.entity.components;

import game.graphics.sprite.Sprite;
import game.graphics.texture.Texture;
import game.state.battle.entity.Entity;
import game.state.battle.entity.component.UpdatableComponent;

import java.time.Duration;

public class AnimationComponent implements UpdatableComponent {
    int animationX = 0;
    int animationY = 0;
    int animationWidth = 64;
    int animationHeight = 64;

    Duration animationTimer = Duration.ofMillis(300);

    @Override
    public void onUpdate(Entity self, Duration delta) {
        animationTimer = animationTimer.minus(delta);
        if (animationTimer.isZero() || animationTimer.isNegative()) {
            animationTimer = Duration.ofMillis(300);
            animationX += animationWidth;
            if (animationX / animationWidth >= 4) {
                animationX = 0;
            }
        }
    }

    public Sprite getCurrentSprite(Texture texture) {
        return new Sprite(
                texture,
                animationX,
                animationY,
                animationWidth,
                animationHeight
        );
    }
}
