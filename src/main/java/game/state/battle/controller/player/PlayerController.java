package game.state.battle.controller.player;

import game.state.battle.BattleState;
import game.state.battle.controller.BattleController;
import game.state.battle.entity.Entity;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class PlayerController extends BattleController {
    protected PlayerController(BattleState state) {
        super(state);
    }

    @Override
    public final boolean isDone() {
        List<Entity> playerActorsNotWaiting = state.getWorld().getEntities().stream()
                .filter(Entity::isPlayer)
                .filter(actor -> !actor.isWaiting())
                .toList();

        return playerActorsNotWaiting.isEmpty();
    }

}
