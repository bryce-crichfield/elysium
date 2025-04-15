package game.state.battle.entity.components;

import lombok.Getter;
import lombok.Setter;

public class PositionComponent {
    @Getter
    @Setter
    private int x;

    @Getter
    @Setter
    private int y;

    public PositionComponent(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
