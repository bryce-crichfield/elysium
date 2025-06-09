package sampleGame.battle.util;

import sampleGame.data.entity.Entity;

import java.util.Optional;

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
