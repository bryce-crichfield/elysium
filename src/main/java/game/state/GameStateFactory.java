package game.state;

import game.Game;

@FunctionalInterface
public interface GameStateFactory {
    GameState create(Game game);
}
