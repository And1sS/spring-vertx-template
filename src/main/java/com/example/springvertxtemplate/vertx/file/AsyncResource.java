package com.example.springvertxtemplate.vertx.file;

import io.vertx.core.Future;

public interface AsyncResource<T> {

    Future<T> read();

    Future<Void> write(T data);
}
