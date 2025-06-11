package client.runtime.system;

import client.runtime.config.RuntimeArguments;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class System {
    private final SystemRuntimeContext context;
    private SystemState systemState = SystemState.INACTIVE;
    private Map<SystemFlag, Boolean> flags = Arrays.stream(SystemFlag.values())
            .collect(java.util.stream.Collectors.toMap(flag -> flag, flag -> false));

    public abstract void activate(RuntimeArguments arguments) throws Exception;
    public abstract void deactivate() throws Exception;

    public void setSystemFlag(SystemFlag flag, boolean value) {
        flags.put(flag, value);
    }

    public boolean getSystemFlag(SystemFlag flag) {
        return flags.getOrDefault(flag, false);
    }
}
