package game.state.battle.event;

import game.event.Event;
import game.state.battle.model.Cursor;

public enum CursorMoved {
    ;
    public static final Event<Cursor> event = new Event<>();
}
