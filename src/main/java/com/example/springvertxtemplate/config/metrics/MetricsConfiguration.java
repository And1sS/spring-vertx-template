package com.example.springvertxtemplate.config.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import io.vertx.core.metrics.MetricsOptions;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.ext.dropwizard.Match;
import io.vertx.ext.dropwizard.MatchType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfiguration {

    private static final String METRIC_REGISTRY_NAME = "metric-registry";

    @Bean
    @ConditionalOnProperty(prefix = "metrics", name = "enabled", havingValue = "true")
    public MetricsOptions dropwizardMetricsOptions() {
        return new DropwizardMetricsOptions()
                .setEnabled(true)
                .setRegistryName(MetricsConfiguration.METRIC_REGISTRY_NAME)
                .addMonitoredHttpClientEndpoint(new Match().setValue(".*").setType(MatchType.REGEX));
    }

    @Bean
    @ConditionalOnProperty(prefix = "metrics", name = "enabled", havingValue = "false", matchIfMissing = true)
    public MetricsOptions noOpMetricsOptions() {
        return new MetricsOptions() {
            @Override
            public boolean isEnabled() {
                return false;
            }
        };
    }

    @Bean
    public MetricRegistry metricRegistry() {
        final boolean alreadyExists = SharedMetricRegistries.names().contains(METRIC_REGISTRY_NAME);
        final MetricRegistry metricRegistry = SharedMetricRegistries.getOrCreate(METRIC_REGISTRY_NAME);

        if (!alreadyExists) {
            metricRegistry.register("jvm.gc", new GarbageCollectorMetricSet());
            metricRegistry.register("jvm.memory", new MemoryUsageGaugeSet());
        }

        return metricRegistry;
    }
}
