package game.state.battle.entity.component;

import com.google.gson.JsonObject;

public interface ComponentDeserializer<T extends Component> {
    String getComponentType();
    T deserialize(JsonObject json);
}
