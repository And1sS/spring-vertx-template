package com.example.springvertxtemplate.config;

import com.example.springvertxtemplate.vertx.ContextRunner;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

@Configuration
public class VertxConfiguration {

    @Bean
    public VertxOptions vertxOptions(@Value("${event-loop-threads:#{null}}") @NotNull Integer eventLoopThreads,
                                     @Value("${worker-threads:#{null}}") @NotNull Integer workerThreads) {

        return new VertxOptions()
                .setEventLoopPoolSize(eventLoopThreads)
                // these worker threads are shared among all event loop contexts
                .setWorkerPoolSize(workerThreads);
    }

    @Bean
    public Vertx vertx(VertxOptions vertxOptions) {
        return Vertx.vertx(vertxOptions);
    }

    @Bean
    public EventBus eventBus(Vertx vertx) {
        return vertx.eventBus();
    }

    @Bean
    public ContextRunner contextRunner(Vertx vertx) {
        return new ContextRunner(vertx);
    }
}
