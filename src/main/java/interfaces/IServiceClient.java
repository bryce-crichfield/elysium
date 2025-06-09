package interfaces;

public interface IServiceClient extends IClient {
    IMessage callService(String serviceName, IMessage parameters) throws Exception;
    IMessage callServiceAsync(String serviceName, IMessage parameters, IServiceCallback callback) throws Exception;
}
