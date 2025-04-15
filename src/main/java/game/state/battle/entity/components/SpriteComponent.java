package game.state.battle.entity.components;

import com.google.gson.JsonObject;
import game.Game;
import game.graphics.texture.Sprite;
import game.graphics.texture.SpriteRenderer;
import game.graphics.texture.Texture;
import game.graphics.texture.TextureStore;
import game.state.battle.entity.component.Component;
import game.state.battle.entity.component.ComponentDeserializer;
import game.state.battle.entity.component.JsonSerializable;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class SpriteComponent extends Component implements JsonSerializable {
    private final PositionComponent position;
    private final Texture texture;


    public void onRender(SpriteRenderer renderer) {
        var x = position.getX() * Game.TILE_SIZE;
        var y = position.getY() * Game.TILE_SIZE;

        // TODO: Delegate the sprite selection to an animation component
        Sprite sprite = new Sprite(texture, 0, 0, 64, 64);
        renderer.drawSprite(x, y, Game.TILE_SIZE, Game.TILE_SIZE, sprite);
    }

    @Override
    public JsonObject jsonSerialize() {
        JsonObject json = new JsonObject();
        json.addProperty("texture", texture.getName());
        return json;
    }

    public static class Deserializer implements ComponentDeserializer<SpriteComponent> {

        @Override
        public String getComponentType() {
            return SpriteComponent.class.getSimpleName();
        }

        @Override
        public SpriteComponent deserialize(JsonObject json) {
            String textureName = json.get("texture").getAsString();
            Texture texture = TextureStore.getInstance().getAssets(textureName);
            return new SpriteComponent(new PositionComponent(0, 0), texture);
        }
    }
}
