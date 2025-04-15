package game.platform.gl;

import lombok.Getter;
import org.lwjgl.opengl.GL11;

public class Program {
    // Getters
    @Getter
    private final Shader shader;
    private boolean isRunning;

    public Program(String vertexPath, String fragmentPath) {
        this.shader = new Shader(vertexPath, fragmentPath);
        this.isRunning = false;
    }

    public void start() {
        if (!isRunning) {
            shader.start();
            isRunning = true;
        }
    }

    public void stop() {
        if (isRunning) {
            shader.stop();
            isRunning = false;
        }
    }

    public void dispose() {
        stop();
        shader.dispose();
    }

    public void drawArrays(VertexArray vao, int mode, int first, int count) {
        vao.bind();
        start();
        GL11.glDrawArrays(mode, first, count);
        stop();
        vao.unbind();
    }

    public void setInt(String name, int value) {
        start();
        shader.setInt(name, value);
    }

    public void setMatrix4f(String name, float[] matrix) {
        start();
        shader.setMatrix4f(name, matrix);
    }
}
