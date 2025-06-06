package game.battle.entity.component;

import game.battle.entity.Entity;

import java.time.Duration;

public interface UpdatableComponent extends Component {
    void onUpdate(Entity self, Duration delta);
}
