package coop.stlma.tech.protocolsn.commonlib.exception.grpc;

import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.micronaut.core.annotation.Order;
import io.micronaut.core.order.Ordered;
import jakarta.inject.Singleton;

import java.util.List;

/**
 * Interceptor to handle exceptions thrown by gRPC services.
 *
 * @author John Meyerin
 */
@Singleton
public class ExceptionHandlerInterceptor implements ServerInterceptor {

    private final List<GrpcExceptionHandler> exceptionHandlers;

    public ExceptionHandlerInterceptor(List<GrpcExceptionHandler> exceptionHandlers) {
        this.exceptionHandlers = exceptionHandlers;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {

        ServerCall.Listener<ReqT> delegate = next.startCall(call, headers);

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(delegate) {

            @Override
            public void onHalfClose() {
                try {
                    super.onHalfClose();
                } catch (Exception e) {
                    handleException(call, e);
                    throw e;
                }
            }

            @Override
            public void onReady() {
                try {
                    super.onReady();
                } catch (Exception e) {
                    handleException(call, e);
                    throw e;
                }
            }
        };
    }

    private <RespT, ReqT> void handleException(ServerCall<ReqT, RespT> call, Throwable e) {
        exceptionHandlers.stream().filter(handler -> handler.canHandle(e)).findFirst().ifPresent(handler -> {
            Status status = handler.handleException(e);
            call.close(status, new Metadata());
        });
    }

}
