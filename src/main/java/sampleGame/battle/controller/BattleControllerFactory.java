package sampleGame.battle.controller;

import sampleGame.battle.BattleScene;

public interface BattleControllerFactory {
    BattleController create(BattleScene state);
}
