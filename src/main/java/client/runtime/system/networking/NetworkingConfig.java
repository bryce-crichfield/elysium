package client.runtime.system.networking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class NetworkingConfig {
    private final String host;
    private final int port;
}
