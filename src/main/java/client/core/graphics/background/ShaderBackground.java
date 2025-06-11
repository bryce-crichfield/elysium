package client.core.graphics.background;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

import client.core.graphics.Renderer;
import client.core.graphics.gl.Program;
import client.core.graphics.gl.VertexArray;
import client.core.graphics.gl.VertexBuffer;
import client.core.util.WatchedFile;
import client.runtime.application.Application;
import java.nio.FloatBuffer;
import java.time.Duration;
import org.lwjgl.BufferUtils;

public class ShaderBackground extends Background {
  private Program shader;
  private int resolutionLocation;
  private int timeLocation;

  private final VertexArray vao;
  private final VertexBuffer vbo;

  // Keep track of total elapsed time
  private float totalTime = 0.0f;

  // Shader file paths
  private final String vertexPath;
  private final String fragmentPath;

  // File watchers
  private final WatchedFile vertexShaderFile;
  private final WatchedFile fragmentShaderFile;

  // Debounce timer
  private long lastReloadTime = 0;
  private static final long RELOAD_COOLDOWN = 500; // milliseconds

  ShaderBackground(String vertexPath, String fragmentPath, int screenWidth, int screenHeight) {
    super(screenWidth, screenHeight);

    // Try to find the shader files
    this.vertexPath = vertexPath;
    this.fragmentPath = fragmentPath;

    // Create file watchers
    this.vertexShaderFile = new WatchedFile(vertexPath);
    this.fragmentShaderFile = new WatchedFile(fragmentPath);

    // Initialize shader
    initShader();

    float[] vertices = {
      // Positions         // Texture Coords
      -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, // Top-left
      -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, // Bottom-left
      1.0f, -1.0f, 0.0f, 1.0f, 0.0f, // Bottom-right
      -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, // Top-left
      1.0f, -1.0f, 0.0f, 1.0f, 0.0f, // Bottom-right
      1.0f, 1.0f, 0.0f, 1.0f, 1.0f // Top-right
    };

    FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
    buffer.put(vertices);
    buffer.flip();

    vao = new VertexArray();
    var builder = new VertexBuffer.Builder(vao, GL_ARRAY_BUFFER);
    vbo =
        builder
            .addAttribute(3, GL_FLOAT) // Position
            .addAttribute(2, GL_FLOAT) // Texture Coords
            .build(vertices, GL_STATIC_DRAW);
  }

  private void initShader() {
    // Create shader program
    var vertPath = vertexShaderFile.getAbsolutePath().toString();
    var fragPath = fragmentShaderFile.getAbsolutePath().toString();

    try {
      var newShader = new Program(vertPath, fragPath);
      if (shader != null) {
        shader.dispose();
      }
      shader = newShader;
    } catch (Exception e) {
      System.err.println("Error loading shader: " + e.getMessage());
      return;
    }

    // Get uniform locations
    resolutionLocation = shader.getUniformLocation("resolution");
    timeLocation = shader.getUniformLocation("time");
  }

  @Override
  public void update(Duration delta) {
    // Accumulate time to get continuous animation
    totalTime += delta.toNanos() / 1_000_000_000f;

    // Check for shader file changes
    checkForShaderChanges();
  }

  private void checkForShaderChanges() {
    long currentTime = System.currentTimeMillis();
    if (currentTime - lastReloadTime < RELOAD_COOLDOWN) {
      return;
    }

    boolean needsReload = vertexShaderFile.hasChanged() || fragmentShaderFile.hasChanged();

    if (!needsReload) return;

    initShader();
    lastReloadTime = currentTime;
  }

  @Override
  public void render(Renderer renderer) {
    // Clear any errors before rendering
    glGetError(); // Clear errors

    // Start the shader
    shader.start();

    // Set uniforms
    shader.setUniform(resolutionLocation, Application.SCREEN_WIDTH, Application.SCREEN_HEIGHT);
    shader.setUniform(timeLocation, totalTime);

    // Bind VAO and draw
    vao.bind();
    shader.drawArrays(vao, GL_TRIANGLES, 0, 6);

    // Unbind VAO and shader
    vao.unbind();
    shader.stop();

    // Check for OpenGL errors
    int error = glGetError();
    if (error != GL_NO_ERROR) {
      System.err.println("OpenGL error in StarShaderBackground: " + error);
    }
  }

  public void dispose() {
    if (shader != null) {
      shader.dispose();
    }
    if (vbo != null) {
      vbo.dispose();
    }
    if (vao != null) {
      vao.dispose();
    }
  }
}
