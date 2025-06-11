package sampleGame.server;

import common.IMessage;
import sampleGame.data.BattleData;

public class BattleTick implements IMessage {
  private final BattleData battleData;

  public BattleTick(BattleData battleData) {
    this.battleData = battleData;
  }

  public BattleData getBattleData() {
    return battleData;
  }

  @Override
  public String toString() {
    // get the checksum of the serialied battle data
    var dataBytes = BattleData.serializeToBytes(battleData);
    int checksum = 0;
    for (byte b : dataBytes) {
      checksum += b;
    }
    checksum = checksum % 256; // limit to 1 byte
    return String.format(
        "BattleTick{checksum=%d, width=%d, height=%d}",
        checksum, battleData.getWidth(), battleData.getHeight());
  }
}
