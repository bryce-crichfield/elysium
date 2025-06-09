package client.runtime.system.logging;

import client.runtime.config.RuntimeArguments;
import client.runtime.system.System;
import client.runtime.system.SystemRuntimeContext;

public class LoggingSystem extends System {
    public LoggingSystem(SystemRuntimeContext runtimeContext) {
        super(runtimeContext);
    }

    @Override
    public void initialize(RuntimeArguments arguments) throws Exception {

    }

    public void println(String message) {
        java.lang.System.out.println(message);
    }
}
