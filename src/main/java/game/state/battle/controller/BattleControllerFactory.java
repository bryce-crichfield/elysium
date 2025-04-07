package game.state.battle.controller;

import game.state.battle.state.BattleState;

public interface BattleControllerFactory {
    BattleController create(BattleState state);
}
