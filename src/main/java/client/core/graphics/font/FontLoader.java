package client.core.graphics.font;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.lwjgl.BufferUtils;

public class FontLoader {

  /**
   * Loads a TrueType font file into memory
   *
   * @param fontName Font name (file name without extension)
   * @return ByteBuffer containing the font data
   */
  public static ByteBuffer loadFont(String fontName) {
    // Make sure the font name has .ttf extension
    String fontPath = fontName;
    if (!fontPath.toLowerCase().endsWith(".ttf")) {
      fontPath = fontPath + ".ttf";
    }

    System.out.println("Attempting to load font: " + fontPath);

    // Try multiple ways to load the font
    try {
      // 1. Try direct file access first
      Path path = Paths.get(fontPath);
      if (Files.exists(path)) {
        System.out.println("Loading from file system: " + path.toAbsolutePath());
        byte[] fontData = Files.readAllBytes(path);
        ByteBuffer fontBuffer = BufferUtils.createByteBuffer(fontData.length);
        fontBuffer.put(fontData);
        fontBuffer.flip();
        return fontBuffer;
      }

      // 2. Try from resources with this class's classloader
      InputStream inputStream = FontLoader.class.getResourceAsStream("/" + fontPath);
      if (inputStream != null) {
        System.out.println("Loading from class resources: /" + fontPath);
        return readToByteBuffer(inputStream);
      }

      // 3. Try from resources without leading slash
      inputStream = FontLoader.class.getResourceAsStream(fontPath);
      if (inputStream != null) {
        System.out.println("Loading from class resources (no leading slash): " + fontPath);
        return readToByteBuffer(inputStream);
      }

      // 4. Try with Thread's context classloader
      inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fontPath);
      if (inputStream != null) {
        System.out.println("Loading from thread context classloader: " + fontPath);
        return readToByteBuffer(inputStream);
      }

      // 5. Try with system classloader
      inputStream = ClassLoader.getSystemResourceAsStream(fontPath);
      if (inputStream != null) {
        System.out.println("Loading from system classloader: " + fontPath);
        return readToByteBuffer(inputStream);
      }

      // 6. Try to find URL and convert to file
      URL url = FontLoader.class.getResource("/" + fontPath);
      if (url != null) {
        try {
          File file = new File(url.toURI());
          if (file.exists()) {
            System.out.println("Loading from URL: " + url);
            try (FileInputStream fis = new FileInputStream(file);
                FileChannel channel = fis.getChannel()) {

              ByteBuffer buffer = BufferUtils.createByteBuffer((int) channel.size());
              channel.read(buffer);
              buffer.flip();
              return buffer;
            }
          }
        } catch (Exception e) {
          System.out.println("Error loading from URL: " + e.getMessage());
        }
      }

      // If we get here, we failed to load the font
      System.err.println("Failed to load font: " + fontPath);
      System.err.println("Looked in:");
      System.err.println("  - " + Paths.get(fontPath).toAbsolutePath());
      System.err.println("  - classpath:/" + fontPath);
      System.err.println("  - classpath:" + fontPath);

      // Show what's actually in the resources
      URL rootURL = FontLoader.class.getResource("/");
      if (rootURL != null) {
        System.out.println("Root resources URL: " + rootURL);
        try {
          File dir = new File(rootURL.toURI());
          System.out.println("Files in resources:");
          listFilesRecursively(dir, "  ");
        } catch (Exception e) {
          System.out.println("Error listing resources: " + e.getMessage());
        }
      } else {
        System.out.println("Couldn't access root resources URL");
      }

      throw new RuntimeException("Font file not found: " + fontPath);

    } catch (IOException e) {
      throw new RuntimeException("Error loading font: " + fontPath, e);
    }
  }

  private static ByteBuffer readToByteBuffer(InputStream inputStream) throws IOException {
    ReadableByteChannel channel = Channels.newChannel(inputStream);
    ByteBuffer buffer = BufferUtils.createByteBuffer(8192);

    while (true) {
      int bytes = channel.read(buffer);
      if (bytes == -1) {
        break;
      }
      if (buffer.remaining() == 0) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(buffer.capacity() * 2);
        buffer.flip();
        newBuffer.put(buffer);
        buffer = newBuffer;
      }
    }

    buffer.flip();
    return buffer;
  }

  private static void listFilesRecursively(File dir, String indent) {
    File[] files = dir.listFiles();
    if (files != null) {
      for (File file : files) {
        System.out.println(indent + file.getName());
        if (file.isDirectory()) {
          listFilesRecursively(file, indent + "  ");
        }
      }
    }
  }
}
