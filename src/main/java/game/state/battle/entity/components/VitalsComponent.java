package game.state.battle.entity.components;

import com.google.gson.JsonObject;
import game.state.battle.entity.Entity;
import game.state.battle.entity.component.Component;
import game.state.battle.entity.component.ComponentDeserializer;

public class VitalsComponent extends Component {
    public int movementPoints = 10;
    public int actionPoints = 10;
    public int health = 10;

    @ComponentDeserializer(type = VitalsComponent.class)
    public static VitalsComponent deserialize(JsonObject json, Entity entity) {
        VitalsComponent vitals = new VitalsComponent();
        vitals.movementPoints = json.get("movementPoints").getAsInt();
        vitals.actionPoints = json.get("actionPoints").getAsInt();
        vitals.health = json.get("health").getAsInt();
        return vitals;
    }

    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("movementPoints", movementPoints);
        json.addProperty("actionPoints", actionPoints);
        json.addProperty("health", health);
        return json;
    }
}
