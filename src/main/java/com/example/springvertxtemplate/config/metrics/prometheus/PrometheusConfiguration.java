package com.example.springvertxtemplate.config.metrics.prometheus;

import com.codahale.metrics.MetricRegistry;
import com.example.springvertxtemplate.metrics.MetricsHandler;
import com.example.springvertxtemplate.vertx.ContextRunner;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.dropwizard.samplebuilder.CustomMappingSampleBuilder;
import io.prometheus.client.dropwizard.samplebuilder.DefaultSampleBuilder;
import io.prometheus.client.dropwizard.samplebuilder.MapperConfig;
import io.prometheus.client.dropwizard.samplebuilder.SampleBuilder;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Configuration
@ConditionalOnExpression("'${metrics.enabled:#{null}}' == 'true' and '${metrics.prometheus.enabled:#{null}}' == 'true'")
public class PrometheusConfiguration {

    @Bean
    public SampleBuilder sampleBuilder(List<MapperConfig> mapperConfigs) {
        return mapperConfigs.isEmpty()
                ? new DefaultSampleBuilder()
                : new CustomMappingSampleBuilder(mapperConfigs);
    }

    @Configuration
    @ConditionalOnBean(PrometheusConfiguration.class)
    public static class PrometheusServerConfiguration {

        @Autowired
        PrometheusMetricsConfiguration prometheusConfiguration;

        @Autowired
        SampleBuilder sampleBuilder;

        @Autowired
        Vertx vertx;

        @Autowired
        MetricRegistry metricRegistry;

        @Autowired
        ContextRunner contextRunner;

        @PostConstruct
        public void runPrometheusMetricsServer() {
            final Router router = Router.router(vertx);
            router.get(prometheusConfiguration.getUrl()).handler(new MetricsHandler());

            CollectorRegistry.defaultRegistry.register(new DropwizardExports(metricRegistry, sampleBuilder));

            final Handler<Promise<HttpServer>> creationHandler = promise -> vertx.createHttpServer()
                    .requestHandler(router)
                    .listen(prometheusConfiguration.getPort(), promise);

            contextRunner.runOnNewContext(creationHandler, 6000);
        }
    }

    @Data
    @Validated
    @Component
    @NoArgsConstructor
    @ConfigurationProperties(prefix = "metrics.prometheus")
    private static class PrometheusMetricsConfiguration {

        @NotNull
        Integer port;

        @NotEmpty
        String url;
    }
}
