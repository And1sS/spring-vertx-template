package com.example.springvertxtemplate.config.metrics.prometheus;

import io.prometheus.client.dropwizard.samplebuilder.MapperConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConditionalOnBean(PrometheusConfiguration.class)
public class PrometheusMappersConfiguration {

    @Bean
    public MapperConfig userMetricsMapperConfig() {
        return new MapperConfig(
                "user.*.*",
                "userMappedMetricName",
                Map.of(
                        "role", "role_${0}",
                        "event_type", "event_${1}"));
    }
}
