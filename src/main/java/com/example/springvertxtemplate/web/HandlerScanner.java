package com.example.springvertxtemplate.web;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import lombok.Value;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HandlerScanner {

    public static List<RouteHandlerInfo> scan(VertxHttpHandler handler) {
        return Arrays.stream(handler.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(RouteHandler.class))
                // TODO: add validations for method
                .map(method -> toRouteHandlerInfo(method, handler))
                .collect(Collectors.toList());
    }

    private static RouteHandlerInfo toRouteHandlerInfo(Method method, VertxHttpHandler target) {
        final RouteHandler routeHandlerAnnotation = method.getAnnotation(RouteHandler.class);
        final Handler<RoutingContext> handler = routingContext -> {
            try {
                method.invoke(target, routingContext);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };

        return RouteHandlerInfo.of(
                routeHandlerAnnotation.path(),
                HttpMethod.valueOf(routeHandlerAnnotation.method()),
                handler);
    }

    @Value(staticConstructor = "of")
    public static class RouteHandlerInfo {

        String path;

        HttpMethod httpMethod;

        Handler<RoutingContext> handler;
    }
}
