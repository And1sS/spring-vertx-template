package com.example.springvertxtemplate.vertx.file;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import lombok.SneakyThrows;

import java.util.Objects;

public class JsonFile implements AsyncResource<JsonNode> {

    private final AsyncFile resource;
    private final ObjectMapper objectMapper;

    public JsonFile(AsyncFile resource, ObjectMapper objectMapper) {
        this.resource = Objects.requireNonNull(resource);
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    @Override
    public Future<JsonNode> read() {
        return resource.read()
                .map(Buffer::toString)
                .map(buff -> {
                    try {
                        return objectMapper.readTree(buff);
                    } catch (Throwable t) {
                        throw new RuntimeException(t);
                    }
                });
    }

    @Override
    public Future<Void> write(JsonNode data) {
        return resource.write(Buffer.buffer(data.toString()));
    }
}
