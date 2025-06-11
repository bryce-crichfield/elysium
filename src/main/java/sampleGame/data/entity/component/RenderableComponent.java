package sampleGame.data.entity.component;

import client.core.graphics.Renderer;
import client.core.graphics.sprite.SpriteRenderer;
import sampleGame.data.entity.Entity;

public interface RenderableComponent extends Component {
  default void onSpriteRender(Entity self, SpriteRenderer spriteRenderer) {}

  default void onVectorRender(Entity self, Renderer renderer) {}
}
