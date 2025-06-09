package interfaces;

import java.io.Serializable;

public record ServiceResponse(String requestId, Serializable result) implements IMessage {
}
