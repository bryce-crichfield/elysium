package game.state.battle.entity.components;


import com.google.gson.JsonObject;
import game.state.battle.entity.component.Component;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CharacterComponent implements Component {
    private float health;

    public JsonObject serialize() {
        return null;
    }
}
