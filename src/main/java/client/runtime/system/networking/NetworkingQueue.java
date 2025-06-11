package client.runtime.system.networking;

import common.IMessage;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NetworkingQueue {
  private final ConcurrentLinkedQueue<Runnable> serviceResponses = new ConcurrentLinkedQueue<>();
  private final ConcurrentLinkedQueue<IMessage> messages = new ConcurrentLinkedQueue<>();

  public void add(IMessage message) {
    messages.add(message);
  }

  public void addServiceResponse(Runnable response) {
    serviceResponses.add(response);
  }

  public IMessage poll() {
    return messages.poll();
  }

  public boolean isEmpty() {
    return messages.isEmpty();
  }

  public List<Runnable> getRunnables() {
    var runnables = List.copyOf(serviceResponses);
    serviceResponses.clear();
    return runnables;
  }
}
