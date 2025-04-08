package game.state.battle.controller;

import game.state.battle.BattleState;

public interface BattleControllerFactory {
    BattleController create(BattleState state);
}
