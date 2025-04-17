package game.state.battle.entity.components;

import game.Game;
import game.graphics.sprite.Sprite;
import game.graphics.sprite.SpriteRenderer;
import game.graphics.texture.Texture;
import game.graphics.texture.TextureStore;
import game.state.battle.entity.Entity;
import game.state.battle.entity.component.RenderableComponent;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class SpriteComponent implements RenderableComponent {
    private final String textureId;

    @Override
    public void onSpriteRender(Entity self, SpriteRenderer renderer) {
        if (self.lacksComponent(PositionComponent.class)) return;

        var position = self.getComponent(PositionComponent.class);
        var x = position.getX() * Game.TILE_SIZE;
        var y = position.getY() * Game.TILE_SIZE;

        // TODO: Delegate the sprite selection to an animation component
        Texture texture = TextureStore.getInstance().getAssets(textureId);
        Sprite sprite = new Sprite(texture, 0, 0, 64, 64);
        renderer.drawSprite(x, y, Game.TILE_SIZE, Game.TILE_SIZE, sprite);
    }

    @Override
    public String toString() {
        return "{" +
                "textureId='" + textureId + '\'' +
                '}';
    }
}
