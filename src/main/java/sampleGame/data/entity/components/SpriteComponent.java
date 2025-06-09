package sampleGame.data.entity.components;

import client.runtime.application.Application;
import client.core.graphics.sprite.Sprite;
import client.core.graphics.sprite.SpriteRenderer;
import client.core.graphics.texture.Texture;
import client.core.graphics.texture.TextureStore;
import lombok.AllArgsConstructor;
import sampleGame.data.entity.Entity;
import sampleGame.data.entity.component.Component;
import sampleGame.data.entity.component.RenderableComponent;
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
        var x = position.getX() * Application.TILE_SIZE;
        var y = position.getY() * Application.TILE_SIZE;

        // TODO: Delegate the sprite selection to an animation component
        Texture texture = TextureStore.getInstance().getAssets(textureId);
        Sprite sprite = animation.getCurrentSprite(texture);
        renderer.drawSprite(x, y, Application.TILE_SIZE, Application.TILE_SIZE, sprite);
    }

    @Override
    public String toString() {
        return "{" +
                "textureId='" + textureId + '\'' +
                '}';
    }

    @Override
    public Component clone() {
        return new SpriteComponent(textureId);
    }
}
