package sampleGame.data.entity.components;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sampleGame.data.entity.component.Component;

@Getter
@AllArgsConstructor
public class CharacterComponent implements Component {
  private float health;

  @Override
  public Component clone() {
    return new CharacterComponent(health);
  }
}
