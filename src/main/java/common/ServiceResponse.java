package common;

import java.io.Serializable;

public record ServiceResponse(String requestId, Try<Serializable> result) implements IMessage {
}
