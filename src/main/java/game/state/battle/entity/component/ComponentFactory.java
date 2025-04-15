package game.state.battle.entity.component;

import java.util.Map;

@FunctionalInterface
public interface ComponentFactory {
    Object create(Map<String, Object> data);
}
