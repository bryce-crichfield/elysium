package sampleGame.data.entity.component;

import client.core.input.Keyboard;
import sampleGame.data.entity.Entity;

public interface KeyboardComponent extends Component {
  void onKeyboard(Entity self, Keyboard keyboard);
}
