package game.platform.gl;

import game.Game;
import game.input.KeyEvent;
import game.input.MouseEvent;
import game.platform.Window;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.nio.IntBuffer;
import java.util.Optional;

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
        // Try this instead
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bufferWidth, bufferHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
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
        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            KeyEvent event = new KeyEvent(key, action);
            switch (action) {
                case GLFW_PRESS -> game.getKeyboard().keyPressed(event);
                case GLFW_RELEASE -> game.getKeyboard().keyReleased(event);
                case GLFW_REPEAT -> {}
                default -> {}
            }
        });

        boolean[] isDown = new boolean[GLFW_MOUSE_BUTTON_LAST + 1];
        // Mouse button callback
        glfwSetMouseButtonCallback(windowHandle, (window, button, action, mods) -> {
            // Get cursor position
            double[] xpos = new double[1];
            double[] ypos = new double[1];
            glfwGetCursorPos(window, xpos, ypos);

            Optional<Point> point = transformWindowPointToViewport((int) xpos[0], (int) ypos[0]);
            if (point.isEmpty()) {
                return;
            }

            switch (action) {
                case GLFW_PRESS -> {
                    var event = new MouseEvent.Pressed(point.get(), button, 1, false);
                    game.getMouse().mousePressed(event);
                    isDown[button] = true;
                }
                case GLFW_RELEASE -> {
                    var event = new MouseEvent.Released(point.get(), button, false);

                    // If the button was down, fire a click event
                    if (isDown[button]) {

                        game.getMouse().mouseClicked(new MouseEvent.Clicked(point.get(), button, 1, false));
                    }
                    game.getMouse().mouseReleased(event);
                    isDown[button] = false;
                }
                default -> {
                    // Handle other actions if necessary
                }
            }

        });

        glfwSetCursorPosCallback(windowHandle, (window, xpos, ypos) -> {
            double[] xposArray = new double[1];
            double[] yposArray = new double[1];
            glfwGetCursorPos(window, xposArray, yposArray);

            Optional<Point> point = transformWindowPointToViewport((int) xposArray[0], (int) yposArray[0]);
            if (point.isPresent()) {
                var event = new MouseEvent.Moved(point.get(),  false);
                game.getMouse().mouseMoved(event);

                // if a button is down, fire a drag event
                for (int i = 0; i < isDown.length; i++) {
                    if (isDown[i]) {
                        var dragEvent = new MouseEvent.Dragged(point.get(), i, false);
                        game.getMouse().mouseDragged(dragEvent);
                    }
                }
            }
        });

        glfwSetScrollCallback(windowHandle, (window, xoffset, yoffset) -> {
            double[] xpos = new double[1];
            double[] ypos = new double[1];
            glfwGetCursorPos(window, xpos, ypos);
            Optional<Point> point = transformWindowPointToViewport((int) xpos[0], (int) ypos[0]);
            if (point.isPresent()) {
                var event = new MouseEvent.WheelMoved(point.get(), xoffset, yoffset, false);
                game.getMouse().mouseWheelMoved(event);
            }
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
        glClearColor(0.2f, 0.2f, 0.2f, 1.0f); // Dark gray background


        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        glViewport(0, 0, bufferWidth, bufferHeight);

        // Clear the framebuffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        GlRenderer renderer = new GlRenderer(bufferWidth, bufferHeight);
        game.render(renderer);
        renderer.dispose();

        glDisable(GL_SCISSOR_TEST);

        // Now bind back to the default framebuffer and draw a quad with the attached framebuffer color texture
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        // Set the viewport to the letterboxed dimensions
        var letterboxViewport = calculateLetterBoxViewport();
        glViewport(letterboxViewport.x, letterboxViewport.y, letterboxViewport.width, letterboxViewport.height);

        // Clear the main framebuffer (only needed for letterboxing)
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Render the framebuffer texture to a fullscreen quad
        renderFramebufferTexture(textureColorBuffer);

        // Swap buffers
        glfwSwapBuffers(windowHandle);

        // Poll for window events
        glfwPollEvents();
    }

    private Rectangle calculateLetterBoxViewport() {
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

        return new Rectangle(viewportX, viewportY, viewportWidth, viewportHeight);
    }

    private void renderFramebufferTexture(int textureId) {
        // Enable texture
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, textureId);
        glColor4f(1, 1, 1, 1); // Set color to white

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

    private Optional<Point> transformWindowPointToViewport(int x, int y) {
        // Get the letterbox viewport dimensions
        Rectangle viewport = calculateLetterBoxViewport();

        // Check if the point is within the viewport
        if (x < viewport.x || x >= viewport.x + viewport.width ||
                y < viewport.y || y >= viewport.y + viewport.height) {
            // Point is outside the letterboxed viewport
            return Optional.empty();
        }

        // Transform the point from window coordinates to viewport coordinates
        // Map from viewport rectangle to [0, bufferWidth] x [0, bufferHeight]
        int transformedX = (int)((x - viewport.x) * (float)bufferWidth / viewport.width);
        int transformedY = (int)((y - viewport.y) * (float)bufferHeight / viewport.height);

        return Optional.of(new Point(transformedX, transformedY));
    }
}