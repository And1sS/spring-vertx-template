package com.example.springvertxtemplate.resource;

import com.example.springvertxtemplate.kafka.KafkaMessageProducer;
import com.example.springvertxtemplate.web.RouteHandler;
import com.example.springvertxtemplate.web.VertxHttpHandler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.SqlClient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestHttpHandler implements VertxHttpHandler {

    @NonNull
    SqlClient sqlClient;

    @NonNull
    KafkaMessageProducer kafkaMessageProducer;

    @RouteHandler(path = "/", method = "GET")
    public void handleDefaultGet(RoutingContext routingContext) {
        final String message = routingContext.request().getParam("message");
        if (message != null) {
            kafkaMessageProducer.produceMessage("demo-topic", "some-key", message);
        }

        sqlClient.query("select * from test")
                .execute(result -> {
                    final StringBuilder response = new StringBuilder();
                    result.result()
                            .forEach(row -> response.append(row.toJson().encode()));

                    routingContext.response().end(response.toString());
                });
    }
}
