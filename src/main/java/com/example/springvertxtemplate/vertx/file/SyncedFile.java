package com.example.springvertxtemplate.vertx.file;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;

import java.util.Objects;

public class SyncedFile implements AsyncResource<Buffer> {

    private final long delay;
    private final AsyncResource<Buffer> delegate;
    private final AsyncResource<Buffer> replica;
    private final Vertx vertx;

    public SyncedFile(long delay, AsyncResource<Buffer> delegate, AsyncResource<Buffer> replica, Vertx vertx) {
        this.delay = delay;
        this.replica = Objects.requireNonNull(replica);
        this.delegate = Objects.requireNonNull(delegate);
        this.vertx = Objects.requireNonNull(vertx);
    }

    public void scheduleBackgroundFetch() {
        vertx.setPeriodic(delay, timerId -> replica.read().onSuccess(delegate::write));
    }

    @Override
    public Future<Buffer> read() {
        return delegate.read();
    }

    @Override
    public Future<Void> write(Buffer data) {
        return delegate.write(data)
                .flatMap(ignored -> replica.write(data));
    }
}
