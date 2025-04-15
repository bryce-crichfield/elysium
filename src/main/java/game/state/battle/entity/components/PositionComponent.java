package game.state.battle.entity.components;

import com.google.gson.JsonObject;
import game.state.battle.entity.component.Component;
import game.state.battle.entity.component.ComponentDeserializer;
import game.state.battle.entity.component.JsonSerializable;
import lombok.Getter;
import lombok.Setter;

public class PositionComponent extends Component implements JsonSerializable {
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

    @Override
    public JsonObject jsonSerialize() {
        JsonObject json = new JsonObject();
        json.addProperty("x", x);
        json.addProperty("y", y);
        return json;
    }

    public static class Deserializer implements ComponentDeserializer<PositionComponent> {

        @Override
        public String getComponentType() {
            return PositionComponent.class.getSimpleName();
        }

        @Override
        public PositionComponent deserialize(JsonObject json) {
            int x = json.get("x").getAsInt();
            int y = json.get("y").getAsInt();
            return new PositionComponent(x, y);
        }
    }
}
