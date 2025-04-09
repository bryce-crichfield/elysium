package game.audio.processor;

import javax.sound.sampled.AudioFormat;

public abstract class AudioProcessor {
    protected final AudioFormat format;

    protected AudioProcessor(AudioFormat format) {
        this.format = format;
    }

    public abstract void process(byte[] input, byte[] output, int size);
    public abstract boolean isActive();
}
