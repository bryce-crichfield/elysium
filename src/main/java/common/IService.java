package common;

import java.io.Serializable;
import java.util.Optional;

public interface IService<Request extends IMessage, Response extends Serializable> {
  String getName();

  Optional<Response> execute(ServiceContext context, Request request) throws Exception;
}
