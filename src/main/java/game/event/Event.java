package game.event;

import java.util.ArrayList;
import java.util.List;

public class Event<T> {
    private final List<EventListener<T>> listeners;

    public Event() {
        listeners = new ArrayList<>();
    }

    public void addListener(EventListener<T> listener) {
        if (listeners.contains(listener)) {
            return;
        }
        listeners.add(listener);
    }

    public void removeListener(EventListener<T> listener) {
        listeners.remove(listener);
    }

    public void clearListeners() {
        listeners.clear();
    }

    public void fire(T event) {
        for (EventListener<T> listener : listeners) {
            listener.onEvent(event);
        }
    }
}
