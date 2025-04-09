package game.audio.processor;

import javax.sound.sampled.AudioFormat;

public class DelayAudioProcessor extends AudioProcessor {
    private AudioParameter feedback; // 0.0 to 1.0
    private final int delaySamples;
    private final short[] delayBuffer;
    private int writePos = 0;
    private boolean active = true;

    public DelayAudioProcessor(float delayTimeMs, float feedback, AudioFormat format) {
        super(format);
        this.feedback = new AudioParameter(feedback, format);
        this.delaySamples = (int) (delayTimeMs * format.getSampleRate() / 500); // 500ms is stereo sample
        this.delayBuffer = new short[delaySamples];
    }

    long elapsedNanos = 0;
    int count = 0;

    @Override
    public void process(byte[] input, byte[] output, int bufferSize) {
        if (!active) return;

        feedback.update(bufferSize / 2);

        for (int i = 0; i < bufferSize; i += 2) {
            // Read input sample
            short inputSample = (short) ((input[i] & 0xFF) | (input[i + 1] << 8));

            // Calculate delayed sample position
            int readPos = (writePos - delaySamples);
            if (readPos < 0) readPos += delayBuffer.length;

            // Get delayed sample and apply feedback
            short delaySample = (short) (delayBuffer[readPos] * feedback.getValue());

            // Mix with input
            short outputSample = (short) (inputSample + delaySample);
            if (outputSample > Short.MAX_VALUE) outputSample = Short.MAX_VALUE;
            if (outputSample < Short.MIN_VALUE) outputSample = Short.MIN_VALUE;

            // Write to delay buffer
            delayBuffer[writePos] = outputSample;
            writePos = (writePos + 1) % delayBuffer.length;

            // Write to output
            output[i] = (byte) (outputSample & 0xFF);
            output[i + 1] = (byte) ((outputSample >> 8) & 0xFF);
        }


    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
