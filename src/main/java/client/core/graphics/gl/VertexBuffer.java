package client.core.graphics.gl;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import lombok.Value;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

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

  public void createVertexAttribPointer(int attributeNumber, int size, int stride, int offset) {
    GL20.glVertexAttribPointer(attributeNumber, size, GL11.GL_FLOAT, false, stride, offset);
  }

  public void dispose() {
    GL15.glDeleteBuffers(id);
  }

  // Utility class for describing vertex buffer attributes
  public static class Builder {
    private final VertexArray array;
    private final VertexBuffer buffer;
    private final List<VertexAttribute> attributes = new ArrayList<>();
    private int totalAttributeCount = 0;
    private int currentAttributeIndex = 0;
    private int stride = 0;

    public Builder(VertexArray array, int bufferType) {
      this.array = array;
      this.buffer = new VertexBuffer(bufferType);
      buffer.bind();
    }

    public Builder addAttribute(int count, int type) {
      int typeSize = getTypeSize(type);
      int attributeSize = count * typeSize;

      // Create a new attribute with automatic index
      attributes.add(
          new VertexAttribute(currentAttributeIndex++, count, type, attributeSize, stride));

      // Update total count and stride
      totalAttributeCount += count;
      stride += attributeSize;

      return this;
    }

    public VertexBuffer build(float[] data, int usage) {
      // Bind both VAO and VBO
      array.bind();
      buffer.bind();

      // Upload data to GPU
      FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
      buffer.put(data);
      buffer.flip();
      this.buffer.storeData(buffer, usage);

      // Setup all attributes
      for (VertexAttribute attr : attributes) {
        // Enable this attribute in the VAO
        glEnableVertexAttribArray(attr.getIndex());

        // Configure the attribute pointer
        glVertexAttribPointer(
            attr.getIndex(), // attribute index
            attr.getCount(), // number of components
            attr.getType(), // data type
            false, // normalized
            stride, // stride (bytes between vertex starts)
            attr.getOffset() // offset of first component
            );
      }

      // Unbind VAO and VBO
      this.buffer.unbind();
      array.unbind();

      return this.buffer;
    }

    public VertexBuffer build() {
      // Bind both VAO and VBO
      array.bind();
      buffer.bind();

      // Setup all attributes
      for (VertexAttribute attr : attributes) {
        // Enable this attribute in the VAO
        glEnableVertexAttribArray(attr.getIndex());

        // Configure the attribute pointer
        glVertexAttribPointer(
            attr.getIndex(), attr.getCount(), attr.getType(), false, stride, attr.getOffset());
      }

      // Unbind VAO and VBO
      buffer.unbind();
      array.unbind();

      return buffer;
    }

    private int getTypeSize(int type) {
      switch (type) {
        case GL_FLOAT:
          return Float.BYTES;
        case GL_DOUBLE:
          return Double.BYTES;
        case GL_INT:
          return Integer.BYTES;
        case GL_UNSIGNED_INT:
          return Integer.BYTES;
        case GL_SHORT:
          return Short.BYTES;
        case GL_UNSIGNED_SHORT:
          return Short.BYTES;
        case GL_BYTE:
          return Byte.BYTES;
        case GL_UNSIGNED_BYTE:
          return Byte.BYTES;
        default:
          return Float.BYTES;
      }
    }

    @Value
    private static class VertexAttribute {
      int index; // The attribute index
      int count; // Number of components (e.g., 3 for vec3)
      int type; // Data type (e.g., GL_FLOAT)
      int size; // Size in bytes of this attribute
      int offset; // Offset in bytes within the vertex
    }
  }
}
