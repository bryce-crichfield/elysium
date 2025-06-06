package game.battle.controller.player;

import game.battle.BattleState;
import game.battle.controller.BattleController;
import game.battle.entity.Entity;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class PlayerController extends BattleController {
    protected PlayerController(BattleState state) {
        super(state);
    }

    @Override
    public final boolean isDone() {
        List<Entity> playerActorsNotWaiting = state.getScene().getEntities().stream()
//                .filter(Entity::isPlayer)
//                .filter(actor -> !actor.isWaiting())
                .toList();

        return playerActorsNotWaiting.isEmpty();
    }

}
