package game.battle.entity.component;

import core.graphics.Renderer;
import core.graphics.sprite.SpriteRenderer;
import game.battle.entity.Entity;

public interface RenderableComponent extends Component {
    default void onSpriteRender(Entity self, SpriteRenderer spriteRenderer) {
    }

    default void onVectorRender(Entity self, Renderer renderer) {
    }
}
