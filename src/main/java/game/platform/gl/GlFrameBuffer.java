package game.platform.gl;

import game.platform.FrameBuffer;
import game.platform.Renderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.Stack;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;

public class GlFrameBuffer implements FrameBuffer {
    // Static stack to track framebuffer bindings
    private static final Stack<Integer> framebufferStack = new Stack<>();
    // Static stack to track viewport state
    private static final Stack<int[]> viewportStack = new Stack<>();

    static {
        // Check if the stack is already initialized to avoid duplicate initialization
        if (framebufferStack.isEmpty()) {
            // Initialize with default framebuffer (0)
            framebufferStack.push(0);

            // Initialize viewport stack
            int[] viewport = new int[4];
            GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
            viewportStack.push(viewport);
        }
    }

    private final int fboId;
    private final int textureId;
    private final int width;
    private final int height;
    boolean isBound = false;
    private boolean disposed = false;

    public GlFrameBuffer(int width, int height) {
        this.width = width;
        this.height = height;

        // Create framebuffer object
        fboId = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL_FRAMEBUFFER, fboId);

        // Create texture for color attachment
        textureId = GL11.glGenTextures();
        GL11.glBindTexture(GL_TEXTURE_2D, textureId);
        GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);

        // Set texture parameters
        GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        // Attach texture to framebuffer
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureId, 0);

        // Check if framebuffer is complete
        int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
        if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Failed to create framebuffer: Status code " + status);
        }

        // Restore previous framebuffer binding
        GL30.glBindFramebuffer(GL_FRAMEBUFFER, framebufferStack.peek());
    }

    public void bind() {
        // Get the CURRENT viewport BEFORE making any changes
        int[] currentViewport = new int[4];
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, currentViewport);

        // Get the current framebuffer binding
        int[] currentFbo = new int[1];
        GL30.glGetIntegerv(GL30.GL_FRAMEBUFFER_BINDING, currentFbo);

        // Save the current state to stacks
        viewportStack.push(currentViewport);
        framebufferStack.push(currentFbo[0]);

        // Now bind our framebuffer
        GL30.glBindFramebuffer(GL_FRAMEBUFFER, fboId);

        // Set viewport to match our framebuffer size
        GL11.glViewport(0, 0, width, height);

        // Clear the framebuffer when binding
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
    }

    public void unbind() {
        if (!framebufferStack.isEmpty()) {
            // Restore previous framebuffer
            int previousFbo = framebufferStack.pop();
            GL30.glBindFramebuffer(GL_FRAMEBUFFER, previousFbo);

            // Restore previous viewport exactly as it was
            if (!viewportStack.isEmpty()) {
                int[] previousViewport = viewportStack.pop();
                // Log the viewport being restored
                GL11.glViewport(previousViewport[0], previousViewport[1], previousViewport[2], previousViewport[3]);
            }
        }
    }

    public int getFboId() {
        return fboId;
    }

    public int getTextureId() {
        return textureId;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public Renderer createRenderer() {
        // Make sure we're bound without duplicating state
        if (!isBound) {
            bind();
        }

        // Create a new renderer that has a reference to this framebuffer
        var r = new GlRenderer(this);
        // push transform that will invert the y axis
        var t = new GlTransform();
//        t.scale(1, -1);
//        t.translate(0, height);

        r.pushTransform(t);
        return r;
    }



    @Override
    public void dispose() {
        if (!disposed) {
            GL11.glDeleteTextures(textureId);
            GL30.glDeleteFramebuffers(fboId);
            disposed = true;
        }
    }
}