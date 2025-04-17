package game.state.battle.entity.component;

import game.graphics.Renderer;
import game.graphics.sprite.SpriteRenderer;
import game.state.battle.entity.Entity;

public interface RenderableComponent extends Component {
    default void onSpriteRender(Entity self, SpriteRenderer spriteRenderer) {
    }

    default void onVectorRender(Entity self, Renderer renderer) {
    }
}
