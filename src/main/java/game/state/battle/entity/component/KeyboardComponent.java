package game.state.battle.entity.component;

import game.input.Keyboard;
import game.state.battle.entity.Entity;

public interface KeyboardComponent extends Component {
    void onKeyboard(Entity self, Keyboard keyboard);
}
