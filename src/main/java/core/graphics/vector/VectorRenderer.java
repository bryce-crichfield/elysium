package core.graphics.vector;

import core.GameContext;
import core.graphics.gl.Program;
import core.graphics.gl.VertexArray;
import core.graphics.gl.VertexBuffer;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.Stack;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;

public class VectorRenderer {
    // Constants for vector drawing operations
    private static final int DRAW_LINE = 0;
    private static final int DRAW_RECT = 1;
    private static final int DRAW_ROUND_RECT = 2;
    private static final int DRAW_ELLIPSE = 3;
    private static final int FILL_RECT = 4;
    private static final int FILL_ROUND_RECT = 5;
    private static final int FILL_ELLIPSE = 6;
    private static final int DRAW_TEXT = 7;

    // Maximum number of vector shapes in one batch
    private static final int MAX_BATCH_SIZE = 1000;

    private Program shader;
    private VertexArray vao;
    private VertexBuffer vbo;

    private Stack<Matrix4f> transformStack = new Stack<>();
    private Stack<Vector4f> clipStack = new Stack<>();

    private Vector4f color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    // Batch data
    private float[] batchData;
    private int batchCount;

    public VectorRenderer() {
        shader = new Program("shaders/vector/VectorVertex.glsl",
                "shaders/vector/VectorGeometry.glsl",
                "shaders/vector/VectorFragment.glsl");

        // Initialize batch data
        batchData = new float[MAX_BATCH_SIZE * getVertexFloatCount()];
        batchCount = 0;

        // Initialize OpenGL buffers
        initializeBuffers();

        // Initialize stacks with default values
        transformStack.push(new Matrix4f().identity());
        clipStack.push(new Vector4f(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

    private void initializeBuffers() {
        // Create and bind vertex array
        vao = new VertexArray();
        vao.bind();

        var floatArray = new float[MAX_BATCH_SIZE * getVertexFloatCount()];
        vbo = new VertexBuffer.Builder(vao, GL_ARRAY_BUFFER)
                .addAttribute(2, GL_FLOAT) // Position
                .addAttribute(4, GL_FLOAT) // Params
                .addAttribute(1, GL_INT) // Type
                .addAttribute(4, GL_FLOAT) // Color
                .addAttribute(16, GL_FLOAT) // Transform matrix
                .addAttribute(4, GL_FLOAT) // Clip
                .addAttribute(4, GL_FLOAT) // Viewport
                .build(floatArray, GL15.GL_DYNAMIC_DRAW);
    }

    private int getVertexFloatCount() {
        // Position (2) + Params (4) + Type (1) + Color (4) + Translation (16) + Clip (4) + Viewport (4)
        return 2 + 4 + 1 + 4 + 16 + 4 + 4;
    }



    public void fillRect(int x, int y, int width, int height) {
        // Check if batch is full
        if (batchCount >= MAX_BATCH_SIZE) {
            flush();
        }

        // Calculate offset in batch array
        int offset = batchCount * getVertexFloatCount();

        // Add data to batch
        // Position (x1, y1)
        batchData[offset++] = x;
        batchData[offset++] = y;

        // Params (x2, y2, strokeWidth, cap)
        batchData[offset++] = width;
        batchData[offset++] = height;
        batchData[offset++] = 0;
        batchData[offset++] = 0;

        // Type (DRAW_LINE)
        batchData[offset++] = FILL_RECT;

        // Color (r, g, b, a)
        batchData[offset++] = color.x;
        batchData[offset++] = color.y;
        batchData[offset++] = color.z;
        batchData[offset++] = color.w;

        // Translation
        Matrix4f transform = transformStack.peek();
        float[] transformData = new float[16];
        transform.get(transformData);
        for (int i = 0; i < 16; i++) {
            batchData[offset++] = transformData[i];
        }

        // Clip
        Vector4f clip = clipStack.peek();
        batchData[offset++] = clip.x;
        batchData[offset++] = clip.y;
        batchData[offset++] = clip.z;
        batchData[offset++] = clip.w;

        // Viewport
        batchData[offset++] = 0; // x
        batchData[offset++] = 0; // y
        batchData[offset++] = GameContext.SCREEN_WIDTH; // width
        batchData[offset++] = GameContext.SCREEN_HEIGHT; // height

        // Increment batch count
        batchCount++;
    }

    public void flush() {
        if (batchCount == 0) {
            return;
        }

        // Bind shader and buffers
        shader.start();
        vao.bind();
        vbo.bind();

        // Upload vertex data
        FloatBuffer buffer = BufferUtils.createFloatBuffer(batchCount * getVertexFloatCount());
        buffer.put(batchData, 0, batchCount * getVertexFloatCount());
        buffer.flip();
        vbo.storeSubData(buffer, 0);

        // Draw points (geometry shader will convert these to actual geometry)
        GL11.glDrawArrays(GL11.GL_POINTS, 0, batchCount);

        // Unbind
        vbo.unbind();
        vao.unbind();
        shader.stop();

        // Reset batch
        batchCount = 0;
    }

    // Additional methods for setting color, etc.
    public void setColor(float r, float g, float b, float a) {
        color.set(r, g, b, a);
    }

    public void dispose() {
        vao.dispose();
        shader.dispose();
    }
}
