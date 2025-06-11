package sampleGame.server;

import common.IMessage;

public interface BattleAction extends IMessage {
  record GetState() implements BattleAction {}

  record Move(String entityId, int toX, int toY) implements BattleAction {}
}
