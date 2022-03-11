package com.example.springvertxtemplate.resource;

import com.example.springvertxtemplate.kafka.KafkaMessageProducer;
import com.example.springvertxtemplate.web.RouteHandler;
import com.example.springvertxtemplate.web.VertxHttpHandler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.SqlClient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestHttpHandler implements VertxHttpHandler {

    @NonNull
    private final SqlClient sqlClient;

    @NonNull
    private final KafkaMessageProducer kafkaMessageProducer;

    @NonNull
    private final Vertx vertx;

    @RouteHandler(path = "/", method = "GET")
    public void handleDefaultGet(RoutingContext routingContext) {
        vertx.<Void>executeBlocking(promise -> {
            try {
                Thread.sleep(6000);
            } catch (Exception e) {
            }
            promise.complete();
        }).onSuccess(result ->
                sqlClient.query("select * from test")
                        .execute(queryResult -> {
                            final StringBuilder response = new StringBuilder();
                            queryResult.result()
                                    .forEach(row -> response.append(row.toJson().encode()));

                            routingContext.response().end(response.toString());
                        }));

        final String message = routingContext.request().getParam("message");
        if (message != null) {
            kafkaMessageProducer.produceMessage("demo-topic", "some-key", message);
        }
    }
}
