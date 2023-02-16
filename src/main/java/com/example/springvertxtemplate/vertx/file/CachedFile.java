package com.example.springvertxtemplate.vertx.file;


import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

// TODO: Add cache eviction policy
public class CachedFile implements AsyncFile {

    private final AtomicInteger version = new AtomicInteger(0);
    private final ReentrantLock cacheGuard = new ReentrantLock(false);
    private final AsyncResource<Buffer> delegate;
    private final AtomicReference<Buffer> cache;

    public CachedFile(AsyncResource<Buffer> delegate) {
        this.delegate = Objects.requireNonNull(delegate);
        this.cache = new AtomicReference<>();
    }

    @Override
    public Future<Void> write(Buffer data) {
        final int currentVersion = version.incrementAndGet();

        return delegate.write(data)
                .onSuccess(ignored -> writeToCache(data, currentVersion));
    }

    @Override
    public Future<Buffer> read() {
        final Buffer currentCache = cache.get();

        if (currentCache != null) {
            return Future.succeededFuture(currentCache.copy());
        }

        final int currentVersion = version.incrementAndGet();
        return delegate.read()
                .onSuccess(data -> writeToCache(data, currentVersion));
    }

    private void writeToCache(Buffer buffer, int version) {
        final Buffer copy = buffer.copy();
        cacheGuard.lock();
        if (version >= this.version.get()) {
            Buffer currentCache = cache.get();

            while (!cache.compareAndSet(currentCache, copy)) {
                currentCache = cache.get();
            }
        }
        cacheGuard.unlock();
    }
}
