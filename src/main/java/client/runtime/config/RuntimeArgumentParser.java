package client.runtime.config;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.Getter;

public class RuntimeArgumentParser {
  @Getter private final Map<String, String> arguments = new HashMap<>();

  private final Pattern snakeCasePattern = Pattern.compile("^[a-z][a-z0-9_]*$");

  public RuntimeArgumentParser() {}

  public RuntimeArgumentParser define(String key, String value) {
    validateKeyFormat(key);
    arguments.put(key, value);
    return this;
  }

  public void parse(String[] args) throws ArgumentParseException {
    for (int i = 0; i < args.length; i++) {
      if (args[i].startsWith("--")) {
        String key = args[i].substring(2);
        validateKeyFormat(key);

        if (i + 1 >= args.length || args[i + 1].startsWith("--")) {
          throw new ArgumentParseException("Missing value for key: " + key);
        }

        String value = args[i + 1];
        arguments.put(key, value);
        i++; // Skip the value in next iteration
      } else {
        throw new ArgumentParseException(
            "Invalid argument format: " + args[i] + ". Expected format: --key value");
      }
    }
  }

  public final void validateKeyFormat(String key) {
    if (!snakeCasePattern.matcher(key).matches()) {
      throw new IllegalArgumentException("Key must be in snake_case format: " + key);
    }
  }

  public static class ArgumentParseException extends Exception {
    public ArgumentParseException(String message) {
      super(message);
    }
  }
}
