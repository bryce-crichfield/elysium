package game.graphics.gl;

import lombok.Value;

@Value
public class VertexAttribute {
    private final int index;
    private final int offset;
    private final int size;
}
