package core.audio;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.sound.sampled.AudioFormat;

@Getter
@AllArgsConstructor
public class AudioSample {
    private final AudioFormat format;
    private final byte[] data;
}
