package game.platform.gl;

import game.Game;
import game.input.Mouse;
import game.platform.Window;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GlWindow extends Window {
    private long windowHandle;
    private int bufferWidth;
    private int bufferHeight;

    public GlWindow(int width, int height, Game game) {
        super(width, height, game);
        this.bufferWidth = Game.SCREEN_WIDTH;
        this.bufferHeight = Game.SCREEN_HEIGHT;

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure window
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create window
        windowHandle = glfwCreateWindow(width, height, "Game", NULL, NULL);
        if (windowHandle == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Setup keyboard callback
        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            // Here you would translate GLFW key events to your game's keyboard system
            // This is a stub - you'll need to implement proper keyboard handling
        });

        // Setup mouse callbacks
        setupMouseCallbacks(game);

        // Center the window
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(windowHandle, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    windowHandle,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        // Make the OpenGL context current
        glfwMakeContextCurrent(windowHandle);
        // Enable v-sync
        glfwSwapInterval(1);
        // Make the window visible
        glfwShowWindow(windowHandle);

        // Initialize OpenGL
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    @Override
    public void onInit() {

    }

    private void setupMouseCallbacks(Game game) {
        // Mouse button callback
        glfwSetMouseButtonCallback(windowHandle, (window, button, action, mods) -> {
            // Get cursor position
            double[] xpos = new double[1];
            double[] ypos = new double[1];
            glfwGetCursorPos(window, xpos, ypos);

            // Transform coordinates to game space
            // This is a stub - you'll need to implement proper coordinate transformation
            // similar to the transformCoordinates method in AwtWindow
        });

        // Cursor position callback
        glfwSetCursorPosCallback(windowHandle, (window, xpos, ypos) -> {
            // Transform coordinates to game space and forward to game mouse
            // This is a stub - implement proper handling
        });

        // Scroll callback
        glfwSetScrollCallback(windowHandle, (window, xoffset, yoffset) -> {
            // Forward scroll events to game mouse
            // This is a stub - implement proper handling
        });
    }

    @Override
    public void onRender(float updateTime, float renderTime) {
        // Clear the framebuffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Here you would render your game
        // This is a stub - you'd need to implement proper OpenGL rendering
        // instead of using Graphics2D as in the AWT implementation

        // Draw debug stats (you'd need to implement this with OpenGL text rendering)

        // Swap buffers
        glfwSwapBuffers(windowHandle);

        // Poll for window events
        glfwPollEvents();
    }

    @Override
    public void onClose() {
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    // You'll need to implement coordinate transformation methods similar to
    // transformCoordinates and transformMouseEvent in the AWT implementation
}