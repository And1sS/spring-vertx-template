package com.example.springvertxtemplate.config;

import com.example.springvertxtemplate.vertx.ContextRunner;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VertxConfiguration {

    @Bean
    public Vertx vertx(VertxOptions vertxOptions) {
        return Vertx.vertx(vertxOptions);
    }

    @Bean
    public VertxOptions vertxOptions() {
        return new VertxOptions();
    }

    @Bean
    public ContextRunner contextRunner(Vertx vertx) {
        return new ContextRunner(vertx);
    }
}
