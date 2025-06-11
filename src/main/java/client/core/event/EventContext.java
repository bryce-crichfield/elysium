package client.core.event;

import java.util.ArrayList;
import java.util.List;

public class EventContext {
  private final List<Runnable> subscriptions;

  public EventContext() {
    this.subscriptions = new ArrayList<>();
  }

  public <T> SubscriptionBuilderRunClause<T> on(Event<T> event) {
    return new SubscriptionBuilderRunClause<>(event);
  }

  public void clear() {
    for (Runnable subscription : subscriptions) {
      subscription.run();
    }
    subscriptions.clear();
  }

  public class SubscriptionBuilderRunClause<T> {
    Event<T> event;

    public SubscriptionBuilderRunClause(Event<T> event) {
      this.event = event;
    }

    public void run(EventListener<T> listener) {
      event.addListener(listener);
      subscriptions.add(() -> event.removeListener(listener));
    }
  }
}
