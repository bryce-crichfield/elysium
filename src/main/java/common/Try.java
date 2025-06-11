package common;

import java.io.Serializable;

public interface Try<T extends Serializable> extends Serializable {
    default boolean isSuccess() {
        return this instanceof Success;
    }

    default boolean isFailure() {
        return this instanceof Failure;
    }

    // foreach
    default void forEach(java.util.function.Consumer<T> action) {
        if (this instanceof Success<T> success) {
            action.accept(success.value);
        } else {
            // Do nothing for failure
        }
    }

    // map and flat
    default <R extends Serializable> Try<R> compose(java.util.function.Function<T, R> mapper) {
        if (this instanceof Success<T> success) {
            return new Success<>(mapper.apply(success.value));
        } else {
            return new Failure<>(((Failure<T>) this).error);
        }
    }

    default <R extends Serializable> Try<R> flatMap(java.util.function.Function<T, Try<R>> mapper) {
        if (this instanceof Success<T> success) {
            return mapper.apply(success.value);
        } else {
            return new Failure<>(((Failure<T>) this).error);
        }
    }

    default T getValue() {
        if (this instanceof Success<T> success) {
            return success.value;
        } else {
            throw new IllegalStateException("Cannot get value from a failure");
        }
    }

    public static <T extends Serializable> Try<T> success(T value) {
        return new Success<>(value);
    }

    public static <T extends Serializable> Try<T> failure(String error) {
        return new Failure<>(new Exception(error));
    }

    record Success<T extends Serializable>(T value) implements Try<T> {
    }

    record Failure<T extends Serializable>(Throwable error) implements Try<T> {
    }
}
