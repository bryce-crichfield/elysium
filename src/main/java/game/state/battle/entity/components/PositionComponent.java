package game.state.battle.entity.components;

import com.google.gson.JsonObject;
import game.state.battle.entity.Entity;
import game.state.battle.entity.component.Component;
import game.state.battle.entity.component.ComponentDeserializer;
import lombok.Getter;
import lombok.Setter;

public class PositionComponent extends Component {
    @Getter
    @Setter
    private int x;

    @Getter
    @Setter
    private int y;

    public PositionComponent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("x", x);
        json.addProperty("y", y);
        return json;
    }

    @ComponentDeserializer(type = PositionComponent.class)
    public static PositionComponent deserialize(JsonObject json, Entity entity) {
        int x = json.get("x").getAsInt();
        int y = json.get("y").getAsInt();
        return new PositionComponent(x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PositionComponent)) return false;
        PositionComponent other = (PositionComponent) obj;
        return x == other.x && y == other.y;
    }
}
