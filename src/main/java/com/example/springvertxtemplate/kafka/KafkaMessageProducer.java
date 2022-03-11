package com.example.springvertxtemplate.kafka;

public interface KafkaMessageProducer {

    void init();

    void produceMessage(String topic, String key, String message);
}
