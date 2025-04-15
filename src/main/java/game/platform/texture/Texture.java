package game.platform.texture;

import lombok.Getter;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.util.Optional;

public class Texture {
    // Getters
    @Getter
    private int id;
    @Getter
    private int width;
    @Getter
    private int height;
    @Getter
    private int channels;
    private final int target;

    private Optional<ByteBuffer> imageData;
    private Optional<Runnable> freeBuffer;

    public Texture(ByteBuffer imageData, int width, int height, int channels, Runnable freeBuffer) {
        this.width = width;
        this.height = height;
        this.channels = channels;
        this.target = GL11.GL_TEXTURE_2D;
        this.imageData = Optional.of(imageData);
        this.freeBuffer = Optional.of(freeBuffer);
    }

    public void initialize() {
        if (imageData.isEmpty()) {
            throw new IllegalStateException("Image data is not set");
        }
        if (freeBuffer.isEmpty()) {
            throw new IllegalStateException("Free buffer is not set");
        }
        // Generate texture
        id = GL11.glGenTextures();
        bind();

        // Determine format based on channels
        int format = channels == 4 ? GL11.GL_RGBA : GL11.GL_RGB;

        // Upload image data to GPU
        GL11.glTexImage2D(target, 0, format, width, height, 0, format, GL11.GL_UNSIGNED_BYTE, imageData.get());

        // Generate mipmaps
        GL30.glGenerateMipmap(target);

        // Set default parameters
        setDefaultParameters();

        unbind();

        // Free the image data
        freeBuffer.get().run();

        imageData = Optional.empty();
        freeBuffer = Optional.empty();
    }

    private void setDefaultParameters() {
        // Set filtering parameters
        GL11.glTexParameteri(target, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameteri(target, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        // Set wrapping parameters
        GL11.glTexParameteri(target, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(target, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
    }

    public void bindToUnit(int unit) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
        bind();
    }

    public void bind() {
        GL11.glBindTexture(target, id);
    }


    public void unbind() {
        GL11.glBindTexture(target, 0);
    }


    public void setFiltering(int minFilter, int magFilter) {
        bind();
        GL11.glTexParameteri(target, GL11.GL_TEXTURE_MIN_FILTER, minFilter);
        GL11.glTexParameteri(target, GL11.GL_TEXTURE_MAG_FILTER, magFilter);
        unbind();
    }

    public void setWrapping(int wrapS, int wrapT) {
        bind();
        GL11.glTexParameteri(target, GL11.GL_TEXTURE_WRAP_S, wrapS);
        GL11.glTexParameteri(target, GL11.GL_TEXTURE_WRAP_T, wrapT);
        unbind();
    }

    public void dispose() {
        GL11.glDeleteTextures(id);
    }
}