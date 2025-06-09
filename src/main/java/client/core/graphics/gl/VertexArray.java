package client.core.graphics.gl;


import lombok.Getter;

import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;

public class VertexArray {
    @Getter
    private final int id;
    private final java.util.List<VertexBuffer> vbos = new java.util.ArrayList<>();

    public VertexArray() {
        id = glGenVertexArrays();
    }

    public void bind() {
        glBindVertexArray(id);
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    public void enableAttribute(int attributeIndex) {
        glEnableVertexAttribArray(attributeIndex);
    }

    public void addBuffer(VertexBuffer vbo) {
        vbos.add(vbo);
    }

    public void dispose() {
        for (VertexBuffer vbo : vbos) {
            vbo.dispose();
        }
        glDeleteVertexArrays(id);
    }
}

