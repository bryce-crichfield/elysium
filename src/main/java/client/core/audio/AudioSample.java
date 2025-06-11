package client.core.audio;

import javax.sound.sampled.AudioFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AudioSample {
  private final AudioFormat format;
  private final byte[] data;
}
