package game.state.battle.entity.components;


import com.google.gson.JsonObject;
import game.state.battle.entity.Entity;
import game.state.battle.entity.component.Component;
import game.state.battle.entity.component.ComponentDeserializer;
import game.state.character.GameCharacter;
import lombok.Getter;

public class CharacterComponent extends Component {
    @Getter
    private final GameCharacter character;
    @Getter
    private float health;

    public CharacterComponent(GameCharacter character) {
        this.character = character;
    }

    public JsonObject serialize() {
        return null;
    }

    @ComponentDeserializer(type=CharacterComponent.class)
    public static CharacterComponent deserialize(JsonObject json, Entity entity) {
        return null;
    }
}
