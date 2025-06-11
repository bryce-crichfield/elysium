package client.runtime.system;

import client.runtime.config.RuntimeArguments;
import java.util.Arrays;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public abstract class System {
  private final SystemContext context;
  private final Map<SystemFlag, Boolean> flags =
      Arrays.stream(SystemFlag.values())
          .collect(java.util.stream.Collectors.toMap(flag -> flag, flag -> false));
  @Setter private SystemState systemState = SystemState.INACTIVE;

  public abstract void activate(RuntimeArguments arguments) throws Exception;

  public abstract void deactivate() throws Exception;

  public boolean getSystemFlag(SystemFlag flag) {
    return flags.getOrDefault(flag, false);
  }

  public void setSystemFlag(SystemFlag flag, boolean value) {
    flags.put(flag, value);
  }
}
