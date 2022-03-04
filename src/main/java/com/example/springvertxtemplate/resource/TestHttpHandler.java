package com.example.springvertxtemplate.resource;

import com.example.springvertxtemplate.web.RouteHandler;
import com.example.springvertxtemplate.web.VertxHttpHandler;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Component;

@Component
public class TestHttpHandler implements VertxHttpHandler {

    @RouteHandler(path = "/", method = "GET")
    public void handleDefaultGet(RoutingContext routingContext) {
        routingContext.response().end("Hello world!");
    }
}
