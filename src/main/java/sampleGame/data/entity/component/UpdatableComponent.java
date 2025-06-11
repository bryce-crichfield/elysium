package sampleGame.data.entity.component;

import java.time.Duration;
import sampleGame.data.entity.Entity;

public interface UpdatableComponent extends Component {
  void onUpdate(Entity self, Duration delta);
}
