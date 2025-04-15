package game.state.battle.entity.component;

import com.google.gson.JsonObject;

public interface JsonSerializable {
    JsonObject jsonSerialize();
}
