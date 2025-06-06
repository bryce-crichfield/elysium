package core.state;

import core.GameContext;

@FunctionalInterface
public interface GameStateFactory {
    GameState create(GameContext gameContext);
}
