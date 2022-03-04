package com.example.springvertxtemplate.config;

import com.example.springvertxtemplate.vertx.ContextRunner;
import com.example.springvertxtemplate.web.HandlerScanner;
import com.example.springvertxtemplate.web.VertxHttpHandler;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Configuration
@EnableConfigurationProperties
public class WebConfiguration {

    @Bean
    public Router router(Vertx vertx,
                         List<VertxHttpHandler> httpHandlers) {

        final Router router = Router.router(vertx);
        httpHandlers.stream()
                .flatMap(httpHandler -> HandlerScanner.scan(httpHandler).stream())
                .forEach(routeHandlerInfo ->
                        router.route(routeHandlerInfo.getHttpMethod(), routeHandlerInfo.getPath())
                                .handler(routeHandlerInfo.getHandler()));

        return router;
    }

    @Data
    @Validated
    @Component
    @NoArgsConstructor
    @ConfigurationProperties(prefix = "http")
    private static class HttpServerProperties {

        @NotNull
        Integer port;

        @NotNull
        @Min(1)
        Integer serverInstances;
    }

    @Configuration
    public static class HttpServerConfiguration {

        @Autowired
        private Vertx vertx;

        @Autowired
        private ContextRunner contextRunner;

        @Autowired
        HttpServerProperties httpServerProperties;

        @Autowired
        private Router router;


        @PostConstruct
        public void runHttpServer() {
            final Handler<Promise<HttpServer>> creationHandler = promise -> vertx.createHttpServer()
                    .requestHandler(router)
                    .listen(httpServerProperties.port, promise);

            contextRunner.runOnNewContext(creationHandler, httpServerProperties.serverInstances, 6000);
        }
    }
}
