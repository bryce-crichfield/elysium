package game.state.battle.controller;

import game.state.battle.model.Actor;
import game.state.battle.state.BattleState;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
public abstract class PlayerController extends BattleController {
    protected PlayerController(BattleState state) {
        super(state);
    }

    @Override
    public final boolean isDone() {
        List<Actor> playerActorsNotWaiting = state.getWorld().getActors().stream()
                .filter(Actor::isPlayer)
                .filter(actor -> !actor.isWaiting())
                .toList();

        return playerActorsNotWaiting.isEmpty();
    }
}
