package sampleGame.data.entity.components;


import com.google.gson.JsonObject;
import sampleGame.data.entity.component.Component;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CharacterComponent implements Component {
    private float health;


    @Override
    public Component clone() {
        return new CharacterComponent(health);
    }
}
