package game.state.battle.model.components;

import game.Game;
import game.platform.texture.Sprite;
import game.platform.texture.SpriteRenderer;
import game.platform.texture.Texture;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class SpriteComponent {
    private final PositionComponent position;
    private final Texture texture;


    public void onRender(SpriteRenderer renderer) {
        var x = position.getX() * Game.TILE_SIZE;
        var y = position.getY() * Game.TILE_SIZE;

        // TODO: Delegate the sprite selection to an animation component
        Sprite sprite = new Sprite(texture, 0, 0, 64, 64);
        renderer.drawSprite(x, y, Game.TILE_SIZE, Game.TILE_SIZE, sprite);
    }
}
