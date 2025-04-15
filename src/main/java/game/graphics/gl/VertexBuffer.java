package game.graphics.gl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class VertexBuffer {
    private final int id;
    private final int type;

    public VertexBuffer(int type) {
        this.id = GL15.glGenBuffers();
        this.type = type;
    }

    public void bind() {
        GL15.glBindBuffer(type, id);
    }

    public void unbind() {
        GL15.glBindBuffer(type, 0);
    }

    public void storeData(FloatBuffer data, int usage) {
        bind();
        GL15.glBufferData(type, data, usage);
    }

    public void storeData(IntBuffer data, int usage) {
        bind();
        GL15.glBufferData(type, data, usage);
    }

    public void storeSubData(FloatBuffer data, long offset) {
        bind();
        GL15.glBufferSubData(type, offset, data);
    }

    public void createVertexAttribPointer(int attributeNumber, int size,
                                          int stride, int offset) {
        GL20.glVertexAttribPointer(attributeNumber, size, GL11.GL_FLOAT,
                false, stride, offset);
    }

    public void dispose() {
        GL15.glDeleteBuffers(id);
    }
}
