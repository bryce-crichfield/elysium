package game.state.battle.entity.component;

import game.state.battle.entity.Entity;

import java.time.Duration;

public interface UpdatableComponent extends Component {
    void onUpdate(Entity self, Duration delta);
}
