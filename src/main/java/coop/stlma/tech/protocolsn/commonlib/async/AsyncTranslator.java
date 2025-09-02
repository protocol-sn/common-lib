package coop.stlma.tech.protocolsn.commonlib.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Easily manage translations between guava futures and reactive streams
 *
 * @author John Meyerin
 */
public class AsyncTranslator {

    private AsyncTranslator () {}

    public static <T> Publisher<T> toPublisher(ListenableFuture<T> listenableFuture, Executor executor) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Futures.addCallback(listenableFuture, new FutureCallback<>() {
            @Override
            public void onSuccess(T result) {
                future.complete(result);
            }

            @Override
            public void onFailure(Throwable t) {
                future.completeExceptionally(t);
            }
        }, executor);
        return Mono.fromFuture(future);
    }

    public static <T> Publisher<T> toPublisher(CompletableFuture<T> future) {
        return Mono.fromFuture(future);
    }

    public static <T> Future<T> toFuture(Publisher<T> publisher, Executor executor) {
        return Mono.from(publisher).subscribeOn(Schedulers.fromExecutor(executor)).toFuture();
    }
}
