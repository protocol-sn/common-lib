package coop.stlma.tech.protocolsn.commonlib.async;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Named;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@MicronautTest
class AsyncTranslatorTest {

    @Named(TaskExecutors.BLOCKING)
    private Executor executor;

    @Test
    void testToPublisherGuava_happyPath() {
        ExecutorService execService = Executors.newSingleThreadExecutor();
        ListeningExecutorService lExecService = MoreExecutors.listeningDecorator(execService);

        ListenableFuture<String> listenableFuture = lExecService.submit(() ->  {
            return "Hello World";
        });

        Publisher<String> result = AsyncTranslator.toPublisher(listenableFuture, executor);

        Assertions.assertEquals("Hello World", Mono.from(result).block());
    }

    @Test
    void testToPublisher_happyPath() {
        CompletableFuture<String> future = new CompletableFuture<>();

        future.completeAsync(() -> "Hello World");

        Publisher<String> result = AsyncTranslator.toPublisher(future);

        Assertions.assertEquals("Hello World", Mono.from(result).block());
    }

    @Test
    void testToFuture_happyPath() throws ExecutionException, InterruptedException, TimeoutException {
        Publisher<String> publisher = Mono.defer(() -> Mono.just("Hello World"));

        Future<String> result = AsyncTranslator.toFuture(publisher, executor);

        Assertions.assertEquals("Hello World", result.get(1, TimeUnit.SECONDS));
    }
}
