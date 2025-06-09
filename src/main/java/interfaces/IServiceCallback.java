package interfaces;

import java.util.function.Consumer;

public interface IServiceCallback {
    static IServiceCallback empty() {
        return new IServiceCallback() {
            @Override
            public void onSuccess(IMessage result) {
                // No-op
            }

            @Override
            public void onError(Exception e) {
                System.err.println("No callback handler provided: " + e.getMessage());
            }
        };
    }

    void onSuccess(IMessage result);
    default void onError(Exception e) {
        System.err.println("Service call failed: " + e.getMessage());
    }

    public static IServiceCallback create(Consumer<IMessage> onSuccess) {
        return new IServiceCallback() {
            @Override
            public void onSuccess(IMessage result) {
                onSuccess.accept(result);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        };
    }
}
