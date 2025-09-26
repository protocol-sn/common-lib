package coop.stlma.tech.protocolsn.commonlib.exception.grpc;

import io.grpc.Status;
import io.micronaut.core.order.Ordered;

public interface GrpcExceptionHandler extends Ordered {

    Status handleException(Throwable e);

    boolean canHandle(Throwable e);
}
