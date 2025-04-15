package game.state.battle.entity.component;

import com.google.gson.JsonObject;
import game.state.battle.entity.Entity;

@FunctionalInterface
public interface ComponentDeserializerFunction<T extends Component> {
    T deserialize(JsonObject json, Entity entity);
}
