package game.battle.entity.components;

import core.GameContext;
import core.graphics.sprite.Sprite;
import core.graphics.sprite.SpriteRenderer;
import core.graphics.texture.Texture;
import core.graphics.texture.TextureStore;
import game.battle.entity.Entity;
import game.battle.entity.component.RenderableComponent;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class SpriteComponent implements RenderableComponent {
    private final String textureId;

    @Override
    public void onSpriteRender(Entity self, SpriteRenderer renderer) {
        if (self.lacksComponent(PositionComponent.class)) return;
        if (self.lacksComponent(AnimationComponent.class)) return;

        var position = self.getComponent(PositionComponent.class);
        var animation = self.getComponent(AnimationComponent.class);
        var x = position.getX() * GameContext.TILE_SIZE;
        var y = position.getY() * GameContext.TILE_SIZE;

        // TODO: Delegate the sprite selection to an animation component
        Texture texture = TextureStore.getInstance().getAssets(textureId);
        Sprite sprite = animation.getCurrentSprite(texture);
        renderer.drawSprite(x, y, GameContext.TILE_SIZE, GameContext.TILE_SIZE, sprite);
    }

    @Override
    public String toString() {
        return "{" +
                "textureId='" + textureId + '\'' +
                '}';
    }
}
