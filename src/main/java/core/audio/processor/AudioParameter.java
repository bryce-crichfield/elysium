package core.audio.processor;

import core.audio.automation.AudioAutomation;

import javax.sound.sampled.AudioFormat;

public class AudioParameter {
    private float value;
    private AudioAutomation audioAutomation;
    private long sampleCounter = 0;
    private final AudioFormat format;

    public AudioParameter(float initialValue, AudioFormat format) {
        this.value = initialValue;
        this.format = format;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public void setAutomation(AudioAutomation audioAutomation) {
        this.audioAutomation = audioAutomation;
        this.sampleCounter = 0;
    }

    public void clearAutomation() {
        this.audioAutomation = null;
    }

    // Call this during audio processing
    public void update(int numSamples) {
        if (audioAutomation != null) {
            sampleCounter += numSamples;
            float timeInSeconds = sampleCounter / format.getSampleRate();
            value = audioAutomation.getValue(timeInSeconds);

            // Remove automation when it's complete
            if (audioAutomation.isComplete(timeInSeconds)) {
                audioAutomation = null;
            }
        }
    }
}
