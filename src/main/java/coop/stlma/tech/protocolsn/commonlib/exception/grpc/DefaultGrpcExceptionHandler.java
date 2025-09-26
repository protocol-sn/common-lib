package coop.stlma.tech.protocolsn.commonlib.exception.grpc;

import io.grpc.Status;
import io.micronaut.core.order.Ordered;
import jakarta.inject.Singleton;

/**
 * The default handler of GRPC exceptions. It will handle all exceptions that are not handled by other handlers.
 *
 * @author John Meyerin
 */
@Singleton
public class DefaultGrpcExceptionHandler implements GrpcExceptionHandler{
    @Override
    public Status handleException(Throwable e) {
        return Status.fromThrowable(e)
                //.withCause(e)
                .withDescription(e.getMessage());
    }

    @Override
    public boolean canHandle(Throwable e) {
        return true;
    }

    /**
     * By necessity this must have the lowest precedence so that it does not override other handlers.
     * @return the lowest possible precedence
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
