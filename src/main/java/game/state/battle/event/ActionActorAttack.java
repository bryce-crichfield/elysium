package game.state.battle.event;

import game.event.Event;
import game.state.battle.entity.Entity;
import game.state.battle.world.Tile;

import java.util.List;

public class ActionActorAttack {
    public static final Event<ActionActorAttack> event = new Event<>();
    Entity attacker;
    List<Tile> targets;

    public ActionActorAttack(Entity attacker, List<Tile> targets) {
        this.attacker = attacker;
        this.targets = targets;
    }

    public Entity getAttacker() {
        return attacker;
    }

    public List<Tile> getTargets() {
        return targets;
    }
}
