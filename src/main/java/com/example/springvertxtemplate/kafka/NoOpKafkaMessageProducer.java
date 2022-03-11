package com.example.springvertxtemplate.kafka;

public class NoOpKafkaMessageProducer implements KafkaMessageProducer {

    @Override
    public void init() {
        // NoOp
    }

    @Override
    public void produceMessage(String topic, String key, String message) {
        // NoOp
    }
}
