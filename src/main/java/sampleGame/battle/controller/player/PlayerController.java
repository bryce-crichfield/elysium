package sampleGame.battle.controller.player;

import sampleGame.battle.BattleScene;
import sampleGame.battle.controller.BattleController;
import sampleGame.data.entity.Entity;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class PlayerController extends BattleController {
    protected PlayerController(BattleScene state) {
        super(state);
    }

    @Override
    public final boolean isDone() {
        List<Entity> playerActorsNotWaiting = state.getData().getEntities().stream()
//                .filter(Entity::isPlayer)
//                .filter(actor -> !actor.isWaiting())
                .toList();

        return playerActorsNotWaiting.isEmpty();
    }

}
