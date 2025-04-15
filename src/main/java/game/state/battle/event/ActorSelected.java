package game.state.battle.event;

import game.event.Event;
import game.state.battle.entity.Entity;

public enum ActorSelected {
    ;
    public static final Event<Entity> event = new Event<>();
}
