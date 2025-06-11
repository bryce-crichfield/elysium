package sampleGame.data.entity.components;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import sampleGame.data.entity.component.Component;

@Setter
@Getter
@AllArgsConstructor
public class PositionComponent implements Component {
  private float x;
  private float y;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof PositionComponent other)) return false;
    return x == other.x && y == other.y;
  }

  @Override
  public String toString() {
    return "{" + "x=" + x + ", y=" + y + '}';
  }

  @Override
  public Component clone() {
    return new PositionComponent(x, y);
  }
}
