package com.example.springvertxtemplate.config;

import com.example.springvertxtemplate.kafka.KafkaMessageProducer;
import com.example.springvertxtemplate.vertx.ContextRunner;
import io.vertx.core.Vertx;
import io.vertx.kafka.client.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {

    @Bean
    public KafkaProducer<String, String> kafkaProducer(Vertx vertx) {
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("acks", "1");

        return KafkaProducer.create(vertx, config);
    }

    @Configuration
    public static class KafkaProducerConfiguration {

        @Autowired
        ContextRunner contextRunner;

        @Autowired
        KafkaMessageProducer kafkaMessageProducer;

        @PostConstruct
        public void startKafkaPeriodicProducer() {
            contextRunner.<Void>runOnServiceContext(promise -> {
                kafkaMessageProducer.init();
                promise.complete();
            }, 5000);
        }
    }
}
