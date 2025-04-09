package game.audio;

import game.audio.processor.AudioProcessor;
import game.audio.processor.SampleAudioProcessor;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AudioEngine implements Runnable {
    private final List<AudioProcessor> processors = new ArrayList<>();
    private volatile boolean running = false;
    private Thread audioThread;
    private SourceDataLine outputLine;
    private final int bufferSize = 4096; // Adjust based on latency needs

    private final AudioFormat format = new AudioFormat(
            44100,   // Sample rate
            16,      // Sample size in bits
            2,       // Channels (stereo)
            true,    // Signed
            false    // Big endian
    );

    public AudioEngine() {
        try {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            outputLine = (SourceDataLine) AudioSystem.getLine(info);
            outputLine.open(format, bufferSize);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if (audioThread == null || !audioThread.isAlive()) {
            running = true;
            outputLine.start();
            audioThread = new Thread(this);
            audioThread.setPriority(Thread.MAX_PRIORITY);
            audioThread.start();
        }
    }

    public void stop() {
        running = false;
        if (audioThread != null) {
            try {
                audioThread.join(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        outputLine.stop();
        outputLine.close();
    }

    public void addProcessor(AudioProcessor processor) {
        synchronized (processors) {
            processors.add(processor);
        }
    }

    public void removeProcessor(AudioProcessor processor) {
        synchronized (processors) {
            processors.remove(processor);
        }
    }

    // Create and trigger a clip
    public SampleAudioProcessor createClip(String filename) throws UnsupportedAudioFileException, IOException {
        SampleAudioProcessor sampler = new SampleAudioProcessor(new File(filename), format);
        addProcessor(sampler);
        return sampler;
    }

    private long elapsedNanos = 0;
    private int count = 0;

    @Override
    public void run() {
        byte[] mixBuffer = new byte[bufferSize];
        byte[] tempBuffer = new byte[bufferSize];

        while (running) {
            // Clear mix buffer
            Arrays.fill(mixBuffer, (byte) 0);

            // Process all active processors
            synchronized (processors) {
                List<AudioProcessor> toRemove = new ArrayList<>();

                var startTimeNanos = System.nanoTime();


                for (AudioProcessor processor : processors) {
                    if (!processor.isActive()) {
                        // Mark inactive processors for removal
                        toRemove.add(processor);
                        continue;
                    }

                    // Clear temp buffer
                    Arrays.fill(tempBuffer, (byte) 0);

                    // Process audio
                    processor.process(mixBuffer, tempBuffer, bufferSize);

                    // Copy temp buffer to mix buffer
                    System.arraycopy(tempBuffer, 0, mixBuffer, 0, bufferSize);
                }

                var endTimeNanos = System.nanoTime();
                elapsedNanos += (endTimeNanos - startTimeNanos);
                if (++count >= 10) {
                    float averageNanos = (float) elapsedNanos / count;
                    float averageMs = averageNanos / 1_000_000;
                    System.out.println("Average delay processing time: " + averageMs + " ms");
                    elapsedNanos = 0;
                    count = 0;
                }

                // Clean up inactive processors
//                    processors.removeAll(toRemove);
            }

            // Write to output line
            outputLine.write(mixBuffer, 0, bufferSize);
        }
    }

    public AudioFormat getFormat() {
        return format;
    }
}

