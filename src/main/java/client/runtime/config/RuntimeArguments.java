package client.runtime.config;

import java.util.Map;

public class RuntimeArguments {
  private final Map<String, String> arguments;

  private RuntimeArguments(String[] args) throws RuntimeArgumentParser.ArgumentParseException {
    var parser = new RuntimeArgumentParser();
    parser.parse(args);
    this.arguments = parser.getArguments();
  }

  public static RuntimeArguments parse(String[] args) {
    try {
      return new RuntimeArguments(args);
    } catch (RuntimeArgumentParser.ArgumentParseException e) {
      throw new RuntimeException("Failed to parse runtime arguments: " + e.getMessage(), e);
    }
  }

  public String getArgument(String key) {
    return arguments.get(key);
  }

  public Map<String, String> getAllArguments() {
    return arguments;
  }

  public boolean hasArgument(String key) {
    return arguments.containsKey(key);
  }

  public void defineArgument(String key, String value) {
    var parser = new RuntimeArgumentParser();
    parser.validateKeyFormat(key);
    arguments.put(key, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("RuntimeArguments{");
    arguments.forEach((key, value) -> sb.append(key).append("=").append(value).append(", "));
    if (!arguments.isEmpty()) {
      sb.setLength(sb.length() - 2); // Remove trailing comma and space
    }
    sb.append("}");
    return sb.toString();
  }

  public boolean isEmpty() {
    return arguments.isEmpty();
  }

  public int size() {
    return arguments.size();
  }

  public String getOrDefault(String key, String defaultValue) {
    return arguments.getOrDefault(key, defaultValue);
  }
}
