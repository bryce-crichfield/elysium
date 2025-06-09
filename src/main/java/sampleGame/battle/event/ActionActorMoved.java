package sampleGame.battle.event;

import client.core.event.Event;
import sampleGame.data.entity.Entity;
import sampleGame.data.tile.Tile;

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
