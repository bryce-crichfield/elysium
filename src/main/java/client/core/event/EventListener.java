package client.core.event;

@FunctionalInterface
public interface EventListener<T> {
  void consume(T event);
}
