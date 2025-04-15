package game.state.battle.entity.component;

import com.google.gson.JsonObject;

public abstract class Component {
    public final String getComponentType() {
        return getClass().getSimpleName();
    }
}
