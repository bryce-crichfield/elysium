package game.audio.processor;

import game.audio.AudioSample;

import javax.sound.sampled.AudioFormat;

public class AudioSampler extends AudioProcessor {
    private final AudioSample sample;
    private int position = 0;
    private boolean active = true;
    private boolean loop = false;
    private final AudioParameter gain;

    private AudioFormat sourceFormat;

    public AudioSampler(AudioSample sample, AudioFormat targetFormat) {
        super(targetFormat);

        this.sample = sample;
        gain = new AudioParameter(1.0f, targetFormat);
    }

    @Override
    public void process(byte[] input, byte[] output, int bufferSize) {
        if (!active || sample.getData() == null) return;

        gain.update(bufferSize / 2);

        for (int i = 0; i < bufferSize; i += 2) {
            if (position >= sample.getData().length) {
                if (loop) {
                    position = 0;
                } else {
                    active = false;
                    break;
                }
            }

            // Mix with input (assuming 16-bit stereo)
            short inputSample = input != null ? (short) ((input[i] & 0xFF) | (input[i + 1] << 8)) : 0;
            short clipSample = (short) ((sample.getData()[position] & 0xFF) | (sample.getData()[position + 1] << 8));

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