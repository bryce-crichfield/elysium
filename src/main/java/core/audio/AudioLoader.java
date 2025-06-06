//package core.audio;
//
//import core.asset.AssetLoader;
//
//import javax.sound.sampled.*;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Path;
//import java.util.HashSet;
//import java.util.Set;
//import java.util.concurrent.atomic.AtomicInteger;
//
//public class AudioAssetLoader extends AssetLoader<String, AudioSample> {
//    // Statistics counters
//    private final AtomicInteger successfullyLoaded = new AtomicInteger(0);
//    private final AtomicInteger failedToLoad = new AtomicInteger(0);
//    private final AtomicInteger convertedAssets = new AtomicInteger(0);
//
//    // Loading state
//    private volatile boolean isLoading = false;
//    private volatile boolean loadingComplete = false;
//
//    // Default conversion format (16-bit PCM stereo at 44.1kHz)
//    private final AudioFormat targetFormat =
//            new AudioFormat(44100.0f, 16, 2, true, false);
//
//    private final AudioStore audioStore;
//
//    public AudioAssetLoader(AudioStore store, Path assetDirectory, AudioFormat format) {
//        super(store, assetDirectory);
//        this.audioStore = store;
//    }
//
//    @Override
//    public Set<String> getExtensions() {
//        Set<String> extensions = new HashSet<>();
//        extensions.add(".wav");
//        extensions.add(".mp3");
//        extensions.add(".ogg");
//        extensions.add(".aiff");
//        return extensions;
//    }
//
//    @Override
//    public boolean load(Runnable callback) {
//        System.out.println("Starting asset loading from: " + super.path);
//        if (isLoading) {
//            System.err.println("Assets are already being loaded");
//            return false;
//        }
//
//        // Reset statistics
//        successfullyLoaded.set(0);
//        failedToLoad.set(0);
//        convertedAssets.set(0);
//
//        // If no assets found
//        if (fileCount == 0) {
//            System.out.println("No audio assets found in: " + super.path);
//            return false;
//        }
//
//        isLoading = true;
//        loadingComplete = false;
//
//        // Start loading in a background thread
//        Thread loadingThread = new Thread(() -> {
//            try {
//                System.out.println("Started loading " + fileCount + " audio assets");
//
//                File file;
//                int processed = 0;
//
//                while ((file = files.poll()) != null) {
//                    processed++;
//                    loadAudioFile(generateKey(file), file, processed);
//                }
//
//                loadingComplete = true;
//                isLoading = false;
//
//                System.out.println("Asset loading complete: " +
//                        successfullyLoaded.get() + "/" + fileCount + " assets loaded successfully");
//                if (convertedAssets.get() > 0) {
//                    System.out.println("Converted " + convertedAssets.get() + " high-resolution audio files");
//                }
//
//                // Run callback if provided
//                if (callback != null) {
//                    callback.run();
//                }
//            } catch (Exception e) {
//                System.err.println("Error during asset loading: " + e.getMessage());
//                isLoading = false;
//            }
//        });
//
//        loadingThread.setName("AudioAssetLoader-Thread");
//        loadingThread.start();
//
//        return true;
//    }
//
//    private String generateKey(File file) {
//        // Generate a key based on the file path relative to the base directory
//        String absolutePath = file.getAbsolutePath();
//        String basePath = super.path.toFile().getAbsolutePath();
//
//        String relativePath = absolutePath.substring(basePath.length() + 1);
//        // Remove file extension
//        String key = relativePath.substring(0, relativePath.lastIndexOf('.'));
//        // Replace backslashes with forward slashes for consistent keys across platforms
//        key = key.replace('\\', '/');
//
//        return key;
//    }
//
//    private void loadAudioFile(String key, File file, int processedCount) {
//        try {
//            Thread.sleep(1);
//            // First try to load the audio file directly
//            AudioInputStream originalStream = AudioSystem.getAudioInputStream(file);
//            AudioFormat sourceFormat = originalStream.getFormat();
//            byte[] data;
//
//            // Check if conversion is needed
//            if (!isFormatCompatible(sourceFormat, targetFormat)) {
//                // Convert to target format
//                AudioInputStream convertedStream = AudioSystem.getAudioInputStream(targetFormat, originalStream);
//                data = readAllBytes(convertedStream);
//                convertedStream.close();
//            } else {
//                // No conversion needed
//                data = readAllBytes(originalStream);
//            }
//            originalStream.close();
//
//            AudioSample sample = new AudioSample(targetFormat, data);
//
//            // If successful, store the clip
//            audioStore.store(key, sample);
//            successfullyLoaded.incrementAndGet();
//
//        } catch (UnsupportedAudioFileException | IOException e) {
//            failedToLoad.incrementAndGet();
//            String errorMsg = "Failed to load: " + key + " - " + e.getMessage();
//            System.err.println(errorMsg);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    /**
//     * Checks if source format is compatible with target format
//     */
//    private boolean isFormatCompatible(AudioFormat source, AudioFormat target) {
//        return source.getSampleRate() == target.getSampleRate() &&
//                source.getSampleSizeInBits() == target.getSampleSizeInBits() &&
//                source.getChannels() == target.getChannels() &&
//                source.isBigEndian() == target.isBigEndian() &&
//                source.getEncoding().equals(target.getEncoding());
//    }
//
//    /**
//     * Reads all bytes from an audio input stream
//     */
//    private byte[] readAllBytes(AudioInputStream stream) throws IOException {
//        byte[] buffer = new byte[4096];
//        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
//        int bytesRead;
//
//        while ((bytesRead = stream.read(buffer)) != -1) {
//            byteStream.write(buffer, 0, bytesRead);
//        }
//
//        return byteStream.toByteArray();
//    }
//}

package core.audio;

import core.asset.AssetLoader;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class AudioLoader extends AssetLoader<String, AudioSample> {
    // Additional statistics counter for converted assets
    private final AtomicInteger convertedAssets = new AtomicInteger(0);

    // Default conversion format (16-bit PCM stereo at 44.1kHz)
    private final AudioFormat targetFormat;

    public AudioLoader(AudioStore store, Path assetDirectory) {
        this(store, assetDirectory, new AudioFormat(44100.0f, 16, 2, true, false));
    }

    public AudioLoader(AudioStore store, Path assetDirectory, AudioFormat format) {
        super(store, assetDirectory, "Audio Assets");
        this.targetFormat = format;
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
    protected AudioSample loadFile(File file, String key) throws Exception {
        // Load the audio file directly
        AudioInputStream originalStream = AudioSystem.getAudioInputStream(file);
        AudioFormat sourceFormat = originalStream.getFormat();
        byte[] data;

        // Check if conversion is needed
        if (!isFormatCompatible(sourceFormat, targetFormat)) {
            // Convert to target format
            AudioInputStream convertedStream = AudioSystem.getAudioInputStream(targetFormat, originalStream);
            data = readAllBytes(convertedStream);
            convertedStream.close();
            convertedAssets.incrementAndGet();
        } else {
            // No conversion needed
            data = readAllBytes(originalStream);
        }
        originalStream.close();

        return new AudioSample(targetFormat, data);
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

    /**
     * Checks if source format is compatible with target format
     */
    private boolean isFormatCompatible(AudioFormat source, AudioFormat target) {
        return source.getSampleRate() == target.getSampleRate() &&
                source.getSampleSizeInBits() == target.getSampleSizeInBits() &&
                source.getChannels() == target.getChannels() &&
                source.isBigEndian() == target.isBigEndian() &&
                source.getEncoding().equals(target.getEncoding());
    }

    /**
     * Reads all bytes from an audio input stream
     */
    private byte[] readAllBytes(AudioInputStream stream) throws IOException {
        byte[] buffer = new byte[4096];
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        int bytesRead;

        while ((bytesRead = stream.read(buffer)) != -1) {
            byteStream.write(buffer, 0, bytesRead);
        }

        return byteStream.toByteArray();
    }

    @Override
    public boolean load(Runnable onComplete) {
        // Reset the converted assets counter
        convertedAssets.set(0);

        // Call the parent load method
        boolean result = super.load(() -> {
            // Add additional logging for audio-specific information
            if (convertedAssets.get() > 0) {
                System.out.println("Converted " + convertedAssets.get() + " high-resolution audio files");
            }

            // Run the original callback if provided
            if (onComplete != null) {
                onComplete.run();
            }
        });

        return result;
    }

    public int getConvertedAssets() {
        return convertedAssets.get();
    }
}