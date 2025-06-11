package sampleGame.battle.controller.player;

import java.util.List;
import lombok.Getter;
import sampleGame.battle.BattleScene;
import sampleGame.battle.controller.BattleController;
import sampleGame.data.entity.Entity;

@Getter
public abstract class PlayerController extends BattleController {
  protected PlayerController(BattleScene state) {
    super(state);
  }

  @Override
  public final boolean isDone() {
    List<Entity> playerActorsNotWaiting =
        state.getData().getEntities().stream()
            //                .filter(Entity::isPlayer)
            //                .filter(actor -> !actor.isWaiting())
            .toList();

    return playerActorsNotWaiting.isEmpty();
  }
}
