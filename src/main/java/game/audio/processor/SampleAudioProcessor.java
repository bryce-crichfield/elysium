package game.audio.processor;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class SampleAudioProcessor extends AudioProcessor {
    private byte[] data;
    private int position = 0;
    private boolean active = true;
    private boolean loop = false;
    private final AudioParameter gain;

    private AudioFormat sourceFormat;

    /**
     * Creates a new AudioSampler that loads and converts audio to the target format
     *
     * @param file         The audio file to load
     * @param targetFormat The desired output format
     */
    public SampleAudioProcessor(File file, AudioFormat targetFormat) {
        super(targetFormat);

        try {
            // Load original audio stream
            AudioInputStream originalStream = AudioSystem.getAudioInputStream(file);
            sourceFormat = originalStream.getFormat();

            // Check if conversion is needed
            if (!isFormatCompatible(sourceFormat, targetFormat)) {
                System.out.println("Converting audio format for: " + file.getName());
                System.out.println("Original format: " + sourceFormat);
                System.out.println("Target format: " + targetFormat);

                // Convert to target format
                AudioInputStream convertedStream = AudioSystem.getAudioInputStream(targetFormat, originalStream);
                data = readAllBytes(convertedStream);
                convertedStream.close();
            } else {
                // No conversion needed
                data = readAllBytes(originalStream);
            }
            originalStream.close();
        } catch (UnsupportedAudioFileException | IOException e) {
            System.err.println("Error loading audio file: " + file.getPath());
            e.printStackTrace();
        }

        gain = new AudioParameter(1.0f, targetFormat);
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
    public void process(byte[] input, byte[] output, int bufferSize) {
        if (!active || data == null) return;

        gain.update(bufferSize / 2);

        for (int i = 0; i < bufferSize; i += 2) {
            if (position >= data.length) {
                if (loop) {
                    position = 0;
                } else {
                    active = false;
                    break;
                }
            }

            // Mix with input (assuming 16-bit stereo)
            short inputSample = input != null ? (short) ((input[i] & 0xFF) | (input[i + 1] << 8)) : 0;
            short clipSample = (short) ((data[position] & 0xFF) | (data[position + 1] << 8));

            // Apply gain
            clipSample = (short) (clipSample * gain.getValue());

            // Mix (simple addition with clipping)
            short mixed = (short) (inputSample + clipSample);
            if (mixed > Short.MAX_VALUE) mixed = Short.MAX_VALUE;
            if (mixed < Short.MIN_VALUE) mixed = Short.MIN_VALUE;

            // Write to output
            output[i] = (byte) (mixed & 0xFF);
            output[i + 1] = (byte) ((mixed >> 8) & 0xFF);

            position += 2;
        }
    }

    public void play() {
        position = 0;
        active = true;
    }

    public void stop() {
        active = false;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public AudioParameter getGain() {
        return gain;
    }

    @Override
    public boolean isActive() {
        return active;
    }
}