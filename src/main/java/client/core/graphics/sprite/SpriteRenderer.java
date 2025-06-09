package client.core.graphics.sprite;

import client.core.graphics.Transform;
import client.core.graphics.gl.Program;
import client.core.graphics.gl.VertexArray;
import client.core.graphics.gl.VertexBuffer;
import client.core.graphics.texture.Texture;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class SpriteRenderer {
    // Max number of sprites in one batch
    private static final int MAX_SPRITES = 50000;
    // Vertices per sprite (4 vertices per quad)
    private static final int VERTICES_PER_SPRITE = 4;
    // Indices per sprite (6 indices per quad - 2 triangles)
    private static final int INDICES_PER_SPRITE = 6;
    // Vertex size in bytes
    private static final int VERTEX_SIZE = 10 * Float.BYTES; // 3 position + 2 texCoord + 4 color + 1 texIndex
    // Max number of texture slots (samplers)
    private static final int MAX_TEXTURE_SLOTS = 16;

    private VertexArray vao;
    private VertexBuffer vertexVertexBuffer;
    private VertexBuffer indexVertexBuffer;
    private Program shader;

    private final float[] vertices;
    private final int[] indices;
    private final Texture[] textures;
    private int spriteCount;
    private int textureSlotIndex;

    private Matrix4f projectionMatrix = new Matrix4f();
    private Matrix4f viewMatrix = new Matrix4f();

    public SpriteRenderer(String vertexShaderPath, String fragmentShaderPath) {
        this.vertices = new float[MAX_SPRITES * VERTICES_PER_SPRITE * (VERTEX_SIZE / Float.BYTES)];
        this.indices = new int[MAX_SPRITES * INDICES_PER_SPRITE];
        this.textures = new Texture[MAX_TEXTURE_SLOTS];
        this.spriteCount = 0;
        this.textureSlotIndex = 0;

        // Create shader program
        this.shader = new Program(vertexShaderPath, fragmentShaderPath);

        // Set up uniforms for texture samplers
        this.shader.start();
        int[] samplers = new int[MAX_TEXTURE_SLOTS];
        for (int i = 0; i < MAX_TEXTURE_SLOTS; i++) {
            samplers[i] = i;
        }
        for (int i = 0; i < MAX_TEXTURE_SLOTS; i++) {
            this.shader.setInt("textures[" + i + "]", i);
        }
        this.shader.stop();

        // Create index buffer (static, same for all quads)
        createIndices();

        // Set up VertexArray and VertexBuffers
        setupBuffers();
    }

    private void createIndices() {
        // Set up indices for quads
        for (int i = 0; i < MAX_SPRITES; i++) {
            // Calculate the indices for this quad
            int offsetArrayIndex = i * INDICES_PER_SPRITE;
            int offsetVertexIndex = i * VERTICES_PER_SPRITE;

            // First triangle
            indices[offsetArrayIndex + 0] = offsetVertexIndex + 0;
            indices[offsetArrayIndex + 1] = offsetVertexIndex + 1;
            indices[offsetArrayIndex + 2] = offsetVertexIndex + 2;

            // Second triangle
            indices[offsetArrayIndex + 3] = offsetVertexIndex + 2;
            indices[offsetArrayIndex + 4] = offsetVertexIndex + 3;
            indices[offsetArrayIndex + 5] = offsetVertexIndex + 0;
        }
    }

    private void setupBuffers() {
        // Create and bind VertexArray
        vao = new VertexArray();
        vao.bind();

        // Create vertex buffer (dynamic)
        vertexVertexBuffer = new VertexBuffer(GL15.GL_ARRAY_BUFFER);
        vertexVertexBuffer.bind();
        vertexVertexBuffer.storeData(BufferUtils.createFloatBuffer(vertices.length), GL15.GL_DYNAMIC_DRAW);

        // Create and setup attribute pointers
        // Position attribute (3 floats)
        vertexVertexBuffer.createVertexAttribPointer(0, 3, VERTEX_SIZE, 0);
        // TexCoord attribute (2 floats)
        vertexVertexBuffer.createVertexAttribPointer(1, 2, VERTEX_SIZE, 3 * Float.BYTES);
        // Color attribute (4 floats)
        vertexVertexBuffer.createVertexAttribPointer(2, 4, VERTEX_SIZE, 5 * Float.BYTES);
        // TexIndex attribute (1 float)
        vertexVertexBuffer.createVertexAttribPointer(3, 1, VERTEX_SIZE, 9 * Float.BYTES);

        // Enable attributes
        vao.enableAttribute(0);
        vao.enableAttribute(1);
        vao.enableAttribute(2);
        vao.enableAttribute(3);

        // Create index buffer (static)
        indexVertexBuffer = new VertexBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER);
        indexVertexBuffer.bind();

        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
        indexBuffer.put(indices);
        indexBuffer.flip();

        indexVertexBuffer.storeData(indexBuffer, GL15.GL_STATIC_DRAW);

        // Add VertexBuffers to VertexArray for cleanup
        vao.addBuffer(vertexVertexBuffer);
        vao.addBuffer(indexVertexBuffer);

        // Unbind
        vertexVertexBuffer.unbind();
        vao.unbind();
    }

    public void begin() {
        // Reset batch state
        spriteCount = 0;
        textureSlotIndex = 0;
    }

    @Getter
    @Setter
    private Color color = Color.WHITE;

    @Getter
    @Setter
    private float rotation = 0.0f;

    public void drawSprite(float x, float y, float width, float height, Sprite sprite) {
        Texture texture = sprite.getTexture();
        float[] textureCoords = sprite.getSubImageCoordinates();
        float[] colorArray = new float[] {
                color.getRed() / 255.0f,
                color.getGreen() / 255.0f,
                color.getBlue() / 255.0f,
                color.getAlpha() / 255.0f
        };


        // Check if batch is full or we need to flush due to texture limits
        if (spriteCount >= MAX_SPRITES || textureSlotIndex >= MAX_TEXTURE_SLOTS) {
            flush();
            begin();
        }

        // Find texture index
        float textureIndex = 0;
        for (int i = 0; i < textureSlotIndex; i++) {
            if (textures[i].getId() == texture.getId()) {
                textureIndex = (float) i;
                break;
            }
        }

        // If texture not found in current batch, add it
        if (textureIndex == 0 && (textureSlotIndex == 0 || textures[0].getId() != texture.getId())) {
            textureIndex = (float) textureSlotIndex;
            textures[textureSlotIndex] = texture;
            textureSlotIndex++;
        }

        // Default texture coords if not provided
        if (textureCoords == null) {
            textureCoords = new float[] {
                    0, 0,  // Bottom-left
                    1, 0,  // Bottom-right
                    1, 1,  // Top-right
                    0, 1   // Top-left
            };
        }

        // Default color if not provided
        if (colorArray == null) {
            colorArray = new float[] { 1, 1, 1, 1 }; // White
        }

        // Calculate vertex positions with rotation
        float cos = (float) Math.cos(rotation);
        float sin = (float) Math.sin(rotation);

        float halfWidth = width / 2.0f;
        float halfHeight = height / 2.0f;

        // Center point
        float centerX = x + halfWidth;
        float centerY = y + halfHeight;

        // Vertex positions relative to center
        float[] positions = new float[] {
                -halfWidth, -halfHeight, 0, // Bottom-left
                halfWidth, -halfHeight, 0,  // Bottom-right
                halfWidth, halfHeight, 0,    // Top-right
                -halfWidth, halfHeight, 0    // Top-left
        };

        // Apply rotation and translation
        for (int i = 0; i < 4; i++) {
            int posIdx = i * 3;
            float vx = positions[posIdx];
            float vy = positions[posIdx + 1];

            // Rotate
            float rx = vx * cos - vy * sin;
            float ry = vx * sin + vy * cos;

            // Translate to final position
            positions[posIdx] = rx + centerX;
            positions[posIdx + 1] = ry + centerY;
        }

        // Calculate vertex array offset
        int offset = spriteCount * VERTICES_PER_SPRITE * (VERTEX_SIZE / Float.BYTES);

        // Add vertices to batch
        for (int i = 0; i < 4; i++) {
            // Position (3 floats)
            vertices[offset++] = positions[i * 3];
            vertices[offset++] = positions[i * 3 + 1];
            vertices[offset++] = positions[i * 3 + 2];

            // Texture coordinates (2 floats)
            vertices[offset++] = textureCoords[i * 2];
            vertices[offset++] = textureCoords[i * 2 + 1];

            // Color (4 floats)
            vertices[offset++] = colorArray[0];
            vertices[offset++] = colorArray[1];
            vertices[offset++] = colorArray[2];
            vertices[offset++] = colorArray[3];

            // Texture index (1 float)
            vertices[offset++] = textureIndex;
        }

        spriteCount++;
    }

    public void flush() {
        if (spriteCount == 0) {
            return;
        }

        // Update vertex buffer with new data
        vertexVertexBuffer.bind();
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(spriteCount * VERTICES_PER_SPRITE * (VERTEX_SIZE / Float.BYTES));
        vertexBuffer.put(vertices, 0, spriteCount * VERTICES_PER_SPRITE * (VERTEX_SIZE / Float.BYTES));
        vertexBuffer.flip();
        vertexVertexBuffer.storeSubData(vertexBuffer, 0);

        // Bind textures
        for (int i = 0; i < textureSlotIndex; i++) {
            textures[i].bindToUnit(i);
        }

        // Set uniforms
        shader.start();
        shader.setMatrix4f("projection", projectionMatrix.get(new float[16]));
        shader.setMatrix4f("view", viewMatrix.get(new float[16]));

        // Draw batched quads
        vao.bind();
        GL11.glDrawElements(GL11.GL_TRIANGLES, spriteCount * INDICES_PER_SPRITE, GL11.GL_UNSIGNED_INT, 0);
        vao.unbind();

        shader.stop();

        // Unbind textures
        for (int i = 0; i < textureSlotIndex; i++) {
            glActiveTexture(GL_TEXTURE0 + i);
            glBindTexture(GL11.GL_TEXTURE_2D, 0);
        }
    }

    public void end() {
        flush();
    }

    public void setProjection(Transform transform) {
        this.projectionMatrix = new Matrix4f(transform.getMatrix());
    }
    public void setView(Transform transform) {
        this.viewMatrix = new Matrix4f(transform.getMatrix());
    }

    public void dispose() {
        vao.dispose();
        shader.dispose();
    }
}