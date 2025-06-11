package sampleGame.data.entity.components;

import client.core.graphics.sprite.Sprite;
import client.core.graphics.texture.Texture;
import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import sampleGame.data.entity.Entity;
import sampleGame.data.entity.component.Component;
import sampleGame.data.entity.component.UpdatableComponent;

@NoArgsConstructor
@AllArgsConstructor
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
    return new Sprite(texture, animationX, animationY, animationWidth, animationHeight);
  }

  @Override
  public Component clone() {
    return new AnimationComponent(
        animationX, animationY, animationWidth, animationHeight, animationTimer);
  }
}
