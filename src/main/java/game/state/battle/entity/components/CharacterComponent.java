package game.state.battle.entity.components;


import game.state.character.GameCharacter;
import lombok.Getter;

public class CharacterComponent {
    @Getter
    private final GameCharacter character;
    @Getter
    private float health;

    public CharacterComponent(GameCharacter character) {
        this.character = character;
    }
}
