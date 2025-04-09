package game.audio;

import game.asset.AssetLoader;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class AudioAssetLoader extends AssetLoader<String, Clip> {
    // Statistics counters
    private final AtomicInteger successfullyLoaded = new AtomicInteger(0);
    private final AtomicInteger failedToLoad = new AtomicInteger(0);
    private final AtomicInteger convertedAssets = new AtomicInteger(0);

    // Loading state
    private volatile boolean isLoading = false;
    private volatile boolean loadingComplete = false;

    // Default conversion format (16-bit PCM stereo at 44.1kHz)
    private final AudioFormat targetFormat =
            new AudioFormat(44100.0f, 16, 2, true, false);

    private final AudioStore audioStore;

    public AudioAssetLoader(AudioStore store, Path assetDirectory) {
        super(store, assetDirectory);
        this.audioStore = store;
    }

    @Override
    public Set<String> getExtensions() {
        Set<String> extensions = new HashSet<>();
        extensions.add(".wav");
        extensions.add(".mp3");
        extensions.add(".ogg");
        extensions.add(".aiff");
        return extensions;
    }

    @Override
    public boolean load(Runnable callback) {
        System.out.println("Starting asset loading from: " + super.path);
        if (isLoading) {
            System.err.println("Assets are already being loaded");
            return false;
        }

        // Reset statistics
        successfullyLoaded.set(0);
        failedToLoad.set(0);
        convertedAssets.set(0);

        // If no assets found
        if (fileCount == 0) {
            System.out.println("No audio assets found in: " + super.path);
            return false;
        }

        isLoading = true;
        loadingComplete = false;

        // Start loading in a background thread
        Thread loadingThread = new Thread(() -> {
            try {
                System.out.println("Started loading " + fileCount + " audio assets");

                File file;
                int processed = 0;

                while ((file = files.poll()) != null) {
                    processed++;
                    loadAudioFile(generateKey(file), file, processed);
                }

                loadingComplete = true;
                isLoading = false;

                System.out.println("Asset loading complete: " +
                        successfullyLoaded.get() + "/" + fileCount + " assets loaded successfully");
                if (convertedAssets.get() > 0) {
                    System.out.println("Converted " + convertedAssets.get() + " high-resolution audio files");
                }

                // Run callback if provided
                if (callback != null) {
                    callback.run();
                }
            } catch (Exception e) {
                System.err.println("Error during asset loading: " + e.getMessage());
                isLoading = false;
            }
        });

        loadingThread.setName("AudioAssetLoader-Thread");
        loadingThread.start();

        return true;
    }

    private String generateKey(File file) {
        // Generate a key based on the file path relative to the base directory
        String absolutePath = file.getAbsolutePath();
        String basePath = super.path.toFile().getAbsolutePath();

        String relativePath = absolutePath.substring(basePath.length() + 1);
        // Remove file extension
        String key = relativePath.substring(0, relativePath.lastIndexOf('.'));
        // Replace backslashes with forward slashes for consistent keys across platforms
        key = key.replace('\\', '/');

        return key;
    }

    private void loadAudioFile(String key, File file, int processedCount) {
        try {
            Thread.sleep(1);
            // First try to load the audio file directly
            AudioInputStream originalStream = AudioSystem.getAudioInputStream(file);
            AudioFormat originalFormat = originalStream.getFormat();

            try {
                // Try to create a clip with the original format
                Clip clip = AudioSystem.getClip();
                clip.open(originalStream);

                // If successful, store the clip
                audioStore.store(key, clip);
                successfullyLoaded.incrementAndGet();

                System.out.println("Loaded: " + key + " (" +
                        processedCount + "/" + fileCount + ")");
            } catch (LineUnavailableException e) {
                // Format not supported, try to convert it
                originalStream.close();

                // Reopen the stream for conversion
                AudioInputStream newStream = AudioSystem.getAudioInputStream(file);
                AudioInputStream convertedStream = convertAudioFormat(newStream, targetFormat);

                // Try to create a clip with the converted format
                Clip clip = AudioSystem.getClip();
                clip.open(convertedStream);

                // If successful, store the clip
                audioStore.store(key, clip);
                successfullyLoaded.incrementAndGet();
                convertedAssets.incrementAndGet();

                System.out.println("Converted and loaded: " + key + " (" +
                        processedCount + "/" + fileCount + ")");
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            failedToLoad.incrementAndGet();
            String errorMsg = "Failed to load: " + key + " - " + e.getMessage();
            System.err.println(errorMsg);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert an audio stream to a new format
     */
    private AudioInputStream convertAudioFormat(AudioInputStream sourceStream, AudioFormat targetFormat)
            throws IOException {
        // First convert to the target format
        AudioInputStream convertedStream = AudioSystem.getAudioInputStream(targetFormat, sourceStream);

        // Read the entire audio into a byte array (this allows us to close the original stream)
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;

        while ((bytesRead = convertedStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }

        // Close the original streams
        convertedStream.close();
        sourceStream.close();

        // Create a new stream from the byte array
        byte[] audioBytes = byteArrayOutputStream.toByteArray();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(audioBytes);

        // Return a new AudioInputStream
        return new AudioInputStream(
                byteArrayInputStream,
                targetFormat,
                audioBytes.length / targetFormat.getFrameSize()
        );
    }
}