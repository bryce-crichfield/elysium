package game.battle.entity.components;

import com.google.gson.JsonObject;
import game.battle.entity.component.Component;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class PositionComponent implements Component {
    private float x;
    private float y;

    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("x", x);
        json.addProperty("y", y);
        return json;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PositionComponent other)) return false;
        return x == other.x && y == other.y;
    }

    @Override
    public String toString() {
        return "{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
