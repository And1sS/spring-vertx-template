package com.example.springvertxtemplate.vertx;

import io.vertx.core.*;
import lombok.NonNull;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class ContextRunner {

    @NonNull
    private final Vertx vertx;

    private final Context serviceContext;

    public ContextRunner(Vertx vertx) {
        this.vertx = vertx;
        this.serviceContext = vertx.getOrCreateContext();
    }

    public <T> void runOnServiceContext(Handler<Promise<T>> task, int timeoutMillis) {
        runOnServiceContext(task, 1, timeoutMillis);
    }

    public <T> void runOnServiceContext(Handler<Promise<T>> task, int count, int timeoutMillis) {
        runOnContext(() -> serviceContext, task, count, timeoutMillis);
    }

    public <T> void runOnNewContext(Handler<Promise<T>> task, int timeoutMillis) {
        runOnNewContext(task, 1, timeoutMillis);
    }

    public <T> void runOnNewContext(Handler<Promise<T>> task, int count, int timeoutMillis) {
        runOnContext(vertx::getOrCreateContext, task, count, timeoutMillis);
    }

    private <T> void runOnContext(Supplier<Context> contextSupplier,
                                  Handler<Promise<T>> task,
                                  int count,
                                  int timeoutMillis) {

        final CountDownLatch latch = new CountDownLatch(count);
        final AtomicBoolean actionFailed = new AtomicBoolean(false);

        final Handler<AsyncResult<T>> resultHandler = (AsyncResult<T> result) -> {
            if (result.failed()) {
                actionFailed.set(true);
            }
            latch.countDown();
        };

        IntStream.range(0, count)
                .mapToObj(i -> contextSupplier.get())
                .forEach(context -> runOnContext(context, task, resultHandler));

        try {
            if (!latch.await(timeoutMillis, TimeUnit.MILLISECONDS) || actionFailed.get()) {
                throw new RuntimeException("Action failed");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> void runOnContext(Context context, Handler<Promise<T>> task, Handler<AsyncResult<T>> onResult) {
        final Promise<T> promise = Promise.promise();
        promise.future().onComplete(onResult);

        context.runOnContext(v -> {
            try {
                task.handle(promise);
            } catch (RuntimeException e) {
                promise.fail(e);
            }
        });
    }
}
