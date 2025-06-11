package server;

import common.IService;
import common.IServices;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Services implements IServices {
    private final ConcurrentHashMap<String, IService> services = new ConcurrentHashMap<>();

    @Override
    public void register(IService service) {
        services.put(service.getName(), service);
        System.out.println("Service registered: " + service.getName());
    }

    @Override
    public void unregister(String name) {
        services.remove(name);
        System.out.println("Service unregistered: " + name);
    }

    @Override
    public IService get(String name) {
        return services.get(name);
    }

    @Override
    public Set<String> getAvailableServices() {
        return services.keySet();
    }

    @Override
    public boolean hasService(String name) {
        return services.containsKey(name);
    }
}
