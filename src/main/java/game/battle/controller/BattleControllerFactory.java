package game.battle.controller;

import game.battle.BattleState;

public interface BattleControllerFactory {
    BattleController create(BattleState state);
}
