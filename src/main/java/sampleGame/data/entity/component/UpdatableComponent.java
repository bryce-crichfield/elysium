package sampleGame.data.entity.component;

import sampleGame.data.entity.Entity;

import java.time.Duration;

public interface UpdatableComponent extends Component {
    void onUpdate(Entity self, Duration delta);
}
