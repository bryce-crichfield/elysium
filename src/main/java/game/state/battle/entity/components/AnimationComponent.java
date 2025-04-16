package game.state.battle.entity.components;

import game.state.battle.entity.Entity;
import game.state.battle.entity.component.UpdatableComponent;
import game.state.battle.Tile;
import game.util.Util;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AnimationComponent implements UpdatableComponent {
    //    private final Entity entity;
    boolean animationEnabled = false;
    float animationPeriod = 0.35f;
    float animationX = 0;
    float animationY = 0;
    float animationTargetX = 0;
    float animationTargetY = 0;
    float animationAccumulator = 0;
    Queue<Tile> animationPath = new LinkedList<>();

    public AnimationComponent() {
    }

    public void start(Entity self, List<Tile> path) {
        animationAccumulator = 0;

        var position = self.getComponent(PositionComponent.class);
        animationX = position.getX();
        animationY = position.getY();

        animationPath = new LinkedList<>(path);

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
    }
}
