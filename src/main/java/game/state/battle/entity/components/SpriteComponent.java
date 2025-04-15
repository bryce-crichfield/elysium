package game.state.battle.entity.components;

import com.google.gson.JsonObject;
import game.Game;
import game.graphics.texture.Sprite;
import game.graphics.texture.SpriteRenderer;
import game.graphics.texture.Texture;
import game.graphics.texture.TextureStore;
import game.state.battle.entity.Entity;
import game.state.battle.entity.component.Component;
import game.state.battle.entity.component.ComponentDeserializer;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class SpriteComponent extends Component {
    private final PositionComponent position;
    private final Texture texture;

    @ComponentDeserializer(type = SpriteComponent.class,
            dependencies = {PositionComponent.class})
    public static SpriteComponent deserialize(JsonObject json, Entity entity) {
        PositionComponent position = entity.getComponent(PositionComponent.class);

        String texturePath = json.get("texture").getAsString();
        Texture texture = TextureStore.getInstance().getAssets(texturePath);

        return new SpriteComponent(position, texture);
    }

    public void onRender(SpriteRenderer renderer) {
        var x = position.getX() * Game.TILE_SIZE;
        var y = position.getY() * Game.TILE_SIZE;

        // TODO: Delegate the sprite selection to an animation component
        Sprite sprite = new Sprite(texture, 0, 0, 64, 64);
        renderer.drawSprite(x, y, Game.TILE_SIZE, Game.TILE_SIZE, sprite);
    }

    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("texture", texture.getName());
        return json;
    }
}
