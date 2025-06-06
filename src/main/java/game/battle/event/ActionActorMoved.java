package game.battle.event;

import core.event.Event;
import game.battle.entity.Entity;
import game.battle.tile.Tile;

import java.util.List;

public class ActionActorMoved {
    public static final Event<ActionActorMoved> event = new Event<>();

    public final Entity entity;
    public final List<Tile> movePath;

    public ActionActorMoved(Entity entity, List<Tile> movePath) {
        this.entity = entity;
        this.movePath = movePath;
    }
}
