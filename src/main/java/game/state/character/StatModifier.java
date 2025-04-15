package game.state.character;

public interface StatModifier {
    StatBlock apply(StatBlock stats);
}
