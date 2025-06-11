package common;

@FunctionalInterface
public interface IServiceCallback {
  void onResponse(ServiceResponse response);
}
