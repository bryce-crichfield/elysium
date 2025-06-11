package sampleGame.data.entity.components;

import client.core.util.Util;
import java.time.Duration;
import java.util.LinkedList;
import java.util.Queue;
import lombok.AllArgsConstructor;
import sampleGame.data.entity.Entity;
import sampleGame.data.entity.component.Component;
import sampleGame.data.entity.component.UpdatableComponent;
import sampleGame.data.tile.Tile;
import sampleGame.data.tile.TilePath;

@AllArgsConstructor
public class TileAnimationComponent implements UpdatableComponent {
  //    private final Entity entity;
  boolean animationEnabled = false;
  float animationPeriod = 0.35f;
  float animationX = 0;
  float animationY = 0;
  float animationTargetX = 0;
  float animationTargetY = 0;
  float animationAccumulator = 0;
  Queue<Tile> animationPath = new LinkedList<>();

  public TileAnimationComponent() {}

  public void start(Entity self, TilePath path) {
    animationAccumulator = 0;

    var position = self.getComponent(PositionComponent.class);
    animationX = position.getX();
    animationY = position.getY();

    animationPath = new LinkedList<>(path.getTiles());

    if (animationPath.isEmpty()) {
      animationTargetX = animationX;
      animationTargetY = animationY;
      return;
    }

    Tile target = animationPath.poll();
    animationTargetX = target.getX();
    animationTargetY = target.getY();

    animationEnabled = true;
  }

  @Override
  public void onUpdate(Entity self, Duration delta) {
    if (!animationEnabled) {
      return;
    }

    float dt = Util.perSecond(delta);
    animationAccumulator += dt;

    if (animationAccumulator >= animationPeriod) {
      animationAccumulator = 0;

      if (!animationPath.isEmpty()) {
        Tile nextTile = animationPath.poll();
        animationTargetX = nextTile.getX();
        animationTargetY = nextTile.getY();
      }
    }

    animationX = Util.easeIn(animationX, animationTargetX, animationPeriod, animationAccumulator);
    animationY = Util.easeIn(animationY, animationTargetY, animationPeriod, animationAccumulator);

    if (Math.abs(animationTargetX - animationX) < 0.1) {
      animationX = animationTargetX;
    }

    if (Math.abs(animationTargetY - animationY) < 0.1) {
      animationY = animationTargetY;
    }

    if (!self.hasComponent(PositionComponent.class)) {
      return;
    }

    var position = self.getComponent(PositionComponent.class);
    position.setX(animationX);
    position.setY(animationY);
  }

  @Override
  public Component clone() {
    return new TileAnimationComponent(
        animationEnabled,
        animationPeriod,
        animationX,
        animationY,
        animationTargetX,
        animationTargetY,
        animationAccumulator,
        new LinkedList<>(animationPath));
  }
}
