package common;

import java.util.function.Consumer;

@FunctionalInterface
public interface IServiceCallback {
    void onResponse(ServiceResponse response);
}
