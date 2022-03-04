package com.example.springvertxtemplate.resource;

import com.example.springvertxtemplate.web.RouteHandler;
import com.example.springvertxtemplate.web.VertxHttpHandler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.SqlClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestHttpHandler implements VertxHttpHandler {

    @Autowired
    SqlClient sqlClient;

    @RouteHandler(path = "/", method = "GET")
    public void handleDefaultGet(RoutingContext routingContext) {
        sqlClient.query("select * from test")
                .execute(result -> {
                    final StringBuilder response = new StringBuilder();
                    result.result()
                            .forEach(row -> response.append(row.toJson().encode()));

                    routingContext.response().end(response.toString());
                });
    }
}
