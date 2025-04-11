package game.platform.gl;

import game.Game;
import game.input.KeyEvent;
import game.platform.Window;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30C.GL_FRAMEBUFFER;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GlWindow extends Window {
    private long windowHandle;
    private int bufferWidth;
    private int bufferHeight;

    private int fbo;
    private int textureColorBuffer;
    private int rbo;

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

        // Setup mouse callbacks
        setupInputCallbacks(game);

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

        // Create the framebuffer that will be used for rendering
        glViewport(0, 0, width, height);
    }

    @Override
    public void onInit() {
        // Create the framebuffer
        fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);

        // Create a texture to attach to the framebuffer
        textureColorBuffer = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureColorBuffer);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, bufferWidth, bufferHeight, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureColorBuffer, 0);

        // Create a renderbuffer object for depth and stencil attachment
        rbo = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rbo);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, bufferWidth, bufferHeight);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo);

        // Check if framebuffer is complete
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Framebuffer is not complete!");
        }

        // Unbind the framebuffer
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }


    private void setupInputCallbacks(Game game) {
        // Setup keyboard callback
        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            // Here you would translate GLFW key events to your game's keyboard system
            // This is a stub - you'll need to implement proper keyboard handling
            System.out.println("Key: " + key + ", Action: " + action);
            KeyEvent event = new KeyEvent(key, action);
            switch (action) {
                case GLFW_PRESS -> game.getKeyboard().keyPressed(event);
                case GLFW_RELEASE -> game.getKeyboard().keyReleased(event);
//                case GLFW_REPEAT -> game.getKeyboard().keyTyped(event);
                default -> {
                    // Handle other actions if necessary
                }
            }
        });


        // Mouse button callback
        glfwSetMouseButtonCallback(windowHandle, (window, button, action, mods) -> {
            // Get cursor position
            double[] xpos = new double[1];
            double[] ypos = new double[1];
            glfwGetCursorPos(window, xpos, ypos);

            switch (action) {
                case GLFW_PRESS -> {
                    // Handle mouse button press
                    System.out.println("Mouse Button Pressed: " + button);
//                    game.getMouse().mousePressed(button, xpos[0], ypos[0]);
                }
            }

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

        // Window resize callback
        glfwSetWindowCloseCallback(windowHandle, window -> {
            // Signal that the window should close
            glfwSetWindowShouldClose(window, true);
            isActive = false;
        });
    }

    private boolean isActive = true;

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void onRender(float updateTime, float renderTime) {
        // First render to our framebuffer
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        glViewport(0, 0, bufferWidth, bufferHeight);

        // Clear the framebuffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Render game content to the framebuffer
        // Your game rendering code goes here
        GlRenderer renderer = new GlRenderer(bufferWidth, bufferHeight);
//        game.render(renderer);
        renderer.fillRect(0, 0, 100, 100);


        // Now bind back to the default framebuffer and draw a quad with the attached framebuffer color texture
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        // Calculate letterboxing dimensions
        int windowWidth = 0, windowHeight = 0;
        try (MemoryStack stack = stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            glfwGetFramebufferSize(windowHandle, widthBuffer, heightBuffer);
            windowWidth = widthBuffer.get(0);
            windowHeight = heightBuffer.get(0);
        }

        // Calculate letterbox dimensions to maintain aspect ratio
        float targetAspectRatio = (float)bufferWidth / bufferHeight;
        float windowAspectRatio = (float)windowWidth / windowHeight;

        int viewportX, viewportY, viewportWidth, viewportHeight;

        if (windowAspectRatio > targetAspectRatio) {
            // Window is wider than target, letterbox left and right
            viewportHeight = windowHeight;
            viewportWidth = (int)(windowHeight * targetAspectRatio);
            viewportX = (windowWidth - viewportWidth) / 2;
            viewportY = 0;
        } else {
            // Window is taller than target, letterbox top and bottom
            viewportWidth = windowWidth;
            viewportHeight = (int)(windowWidth / targetAspectRatio);
            viewportX = 0;
            viewportY = (windowHeight - viewportHeight) / 2;
        }

        // Set the viewport to the letterboxed dimensions
        glViewport(viewportX, viewportY, viewportWidth, viewportHeight);

        // Clear the main framebuffer (only needed for letterboxing)
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Render the framebuffer texture to a fullscreen quad
        renderFramebufferTexture(textureColorBuffer);

        // Swap buffers
        glfwSwapBuffers(windowHandle);

        // Poll for window events
        glfwPollEvents();
    }

    private void renderFramebufferTexture(int textureId) {
        // For simplicity, we'll use immediate mode here
        // In a real implementation, you'd want to use VAOs/VBOs

        // Enable texture
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Draw a textured quad that fills the viewport
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0); glVertex2f(-1, -1);
        glTexCoord2f(1, 0); glVertex2f(1, -1);
        glTexCoord2f(1, 1); glVertex2f(1, 1);
        glTexCoord2f(0, 1); glVertex2f(-1, 1);
        glEnd();

        // Disable texture
        glDisable(GL_TEXTURE_2D);
    }

    @Override
    public void onClose() {
        // Clean up the framebuffer and related resources
        glDeleteFramebuffers(fbo);
        glDeleteTextures(textureColorBuffer);
        glDeleteRenderbuffers(rbo);

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        var errorCallback = glfwSetErrorCallback(null);
        if (errorCallback != null) {
            errorCallback.free();
        }

    }

    // You'll need to implement coordinate transformation methods similar to
    // transformCoordinates and transformMouseEvent in the AWT implementation
}