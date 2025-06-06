package game.battle.entity.component;

import core.input.Keyboard;
import game.battle.entity.Entity;

public interface KeyboardComponent extends Component {
    void onKeyboard(Entity self, Keyboard keyboard);
}
