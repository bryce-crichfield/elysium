package common;

import java.util.Set;

public interface IServices {
    void register(IService service);
    void unregister(String name);
    IService get(String name);
    Set<String> getAvailableServices();
    boolean hasService(String name);
}
