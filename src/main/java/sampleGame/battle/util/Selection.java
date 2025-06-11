package sampleGame.battle.util;

import java.util.Optional;
import sampleGame.data.entity.Entity;

public class Selection {
  private Optional<Entity> selectedActor;

  public Selection() {
    selectedActor = Optional.empty();
  }

  public boolean isPresent() {
    return selectedActor.isPresent();
  }

  public Entity get() {
    if (selectedActor.isEmpty()) {
      throw new IllegalStateException("No actor selected");
    }

    return selectedActor.get();
  }

  public void select(Entity entity) {
    selectedActor = Optional.of(entity);
  }

  public void clear() {
    selectedActor = Optional.empty();
  }
}
