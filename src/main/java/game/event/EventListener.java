package game.event;

@FunctionalInterface
public interface EventListener<T> {
    void consume(T event);
}
