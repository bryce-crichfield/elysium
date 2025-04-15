package game.state.battle.event;

import game.event.Event;
import game.state.battle.entity.Entity;

public enum ActorUnhovered {
    ;
    public static final Event<Entity> event = new Event<>();
}
