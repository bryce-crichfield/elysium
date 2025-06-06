package core.graphics.texture;

import core.asset.AssetLoader;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class TextureLoader extends AssetLoader<String, Texture> {

    public TextureLoader(TextureStore store, Path assetDirectory) {
        super(store, assetDirectory, "Texture Assets");
    }

    @Override
    public Set<String> getExtensions() {
        Set<String> extensions = new HashSet<>();
        extensions.add(".png");
        extensions.add(".jpg");
        extensions.add(".jpeg");
        extensions.add(".bmp");
        extensions.add(".tga");
        return extensions;
    }

    @Override
    protected Texture loadFile(File file, String key) throws Exception {
        // Set up STB to load from file
        STBImage.stbi_set_flip_vertically_on_load(true);

        // Create buffers for width, height, and channels
        IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer channelsBuffer = BufferUtils.createIntBuffer(1);

        // Load image directly from file
        ByteBuffer imageData = STBImage.stbi_load(
                file.getAbsolutePath(), widthBuffer, heightBuffer, channelsBuffer, 0);

        if (imageData == null) {
            throw new IOException("Failed to load image: " + STBImage.stbi_failure_reason());
        }

        // Get image dimensions
        int width = widthBuffer.get(0);
        int height = heightBuffer.get(0);
        int channels = channelsBuffer.get(0);

        // Create the texture
        Texture texture = new Texture(key, imageData, width, height, channels, () -> {
            // Free the image data when the texture is disposed
            STBImage.stbi_image_free(imageData);
        });

        System.out.println("Loaded texture: " + key + " (" + width + "x" + height + ", " + channels + " channels)");

        return texture;
    }

    @Override
    protected String generateKey(File file) {
        // Generate a key based on the file path relative to the base directory
        String absolutePath = file.getAbsolutePath();
        String basePath = path.toFile().getAbsolutePath();

        String relativePath = absolutePath.substring(basePath.length() + 1);
        // Remove file extension
        String key = relativePath.substring(0, relativePath.lastIndexOf('.'));
        // Replace backslashes with forward slashes for consistent keys across platforms
        key = key.replace('\\', '/');

        return key;
    }
}
