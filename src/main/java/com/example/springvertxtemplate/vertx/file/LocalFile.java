package com.example.springvertxtemplate.vertx.file;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;

import java.util.Objects;

public class LocalFile implements AsyncResource<Buffer> {

    private final String path;
    private final FileSystem fileSystem;

    public LocalFile(String path, FileSystem fileSystem) {
        this.path = Objects.requireNonNull(path);
        this.fileSystem = Objects.requireNonNull(fileSystem);
    }

    @Override
    public Future<Buffer> read() {
        return fileSystem.readFile(path);
    }

    @Override
    public Future<Void> write(Buffer data) {
        return fileSystem.writeFile(path, data);
    }
}
