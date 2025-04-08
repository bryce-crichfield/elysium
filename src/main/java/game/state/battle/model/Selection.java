package game.state.battle.model;

import java.util.Optional;

public class Selection {
    private Optional<Actor> selectedActor;

    public Selection() {
        selectedActor = Optional.empty();
    }

    public boolean isPresent() {
        return selectedActor.isPresent();
    }

    public Actor get() {
        if (selectedActor.isEmpty()) {
            throw new IllegalStateException("No actor selected");
        }

        return selectedActor.get();
    }

    public void select(Actor actor) {
        selectedActor = Optional.of(actor);
    }

    public void clear() {
        selectedActor = Optional.empty();
    }
}
