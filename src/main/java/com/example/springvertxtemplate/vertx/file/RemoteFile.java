package com.example.springvertxtemplate.vertx.file;

import com.example.springvertxtemplate.vertx.retrypolicy.MakeRetryPolicy;
import com.example.springvertxtemplate.vertx.retrypolicy.RetryPolicy;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class RemoteFile implements AsyncResource<Buffer> {

    private final Supplier<Future<Buffer>> fetcher;
    private final Function<Buffer, Future<Void>> uploader;
    private final RetryPolicy retryPolicy;
    private final Vertx vertx;

    public RemoteFile(Supplier<Future<Buffer>> fetcher,
                      Function<Buffer, Future<Void>> uploader,
                      RetryPolicy retryPolicy,
                      Vertx vertx) {

        this.fetcher = Objects.requireNonNull(fetcher);
        this.uploader = Objects.requireNonNull(uploader);
        this.retryPolicy = Objects.requireNonNull(retryPolicy);
        this.vertx = Objects.requireNonNull(vertx);
    }

    public RemoteFile(Supplier<Future<Buffer>> fetcher,
                      RetryPolicy retryPolicy,
                      Vertx vertx) {

        this(fetcher, RemoteFile::defaultUploader, retryPolicy, vertx);
    }

    @Override
    public Future<Buffer> read() {
        return fetch(retryPolicy);
    }

    @Override
    public Future<Void> write(Buffer data) {
        return flush(data, retryPolicy);
    }

    private Future<Buffer> fetch(RetryPolicy retryPolicy) {
        return fetcher.get()
                .recover(error -> retryFetch(error, retryPolicy));
    }

    private Future<Buffer> retryFetch(Throwable error, RetryPolicy retryPolicy) {
        if (retryPolicy instanceof MakeRetryPolicy policy) {
            final Promise<Buffer> promise = Promise.promise();
            vertx.setTimer(policy.delay(), timerId -> fetch(policy.next()).onComplete(promise));
            return promise.future();
        } else {
            return Future.failedFuture(error);
        }
    }

    private Future<Void> flush(Buffer buffer, RetryPolicy retryPolicy) {
        return uploader.apply(buffer)
                .recover(error -> retryFlush(error, buffer, retryPolicy));
    }

    private Future<Void> retryFlush(Throwable error, Buffer buffer, RetryPolicy retryPolicy) {
        if (retryPolicy instanceof MakeRetryPolicy policy) {
            final Promise<Void> promise = Promise.promise();
            vertx.setTimer(policy.delay(), timerId -> flush(buffer, policy.next()).onComplete(promise));
            return promise.future();
        } else {
            return Future.failedFuture(error);
        }
    }

    private static Future<Void> defaultUploader(Buffer buffer) {
        return Future.failedFuture(new UnsupportedOperationException());
    }
}
