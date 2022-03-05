package com.example.springvertxtemplate.kafka;

import io.vertx.core.Vertx;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMessageProducer {

    private static final int PROCESSING_THRESHOLD = 10000;
    private static final int MAX_MESSAGES_SIZE = 100000;

    private static final Queue<KafkaProducerRecord<String, String>> messageQueue = new ConcurrentLinkedQueue<>();
    private static final AtomicInteger lastMessageQueueSize = new AtomicInteger();

    @NonNull
    private final Vertx vertx;

    @NonNull
    private final KafkaProducer<String, String> kafkaProducer;

    public void init() {
        vertx.setPeriodic(1000, timerId -> pollAndSendMessages());
    }

    public void produceMessage(String topic, String key, String message) {
        if (messageQueue.size() > MAX_MESSAGES_SIZE) {
            throw new RuntimeException("Maximum allowed messages count exceeded");
        }

        messageQueue.add(KafkaProducerRecord.create(topic, key, message));
    }

    private void pollAndSendMessages() {
        final int processingCount = messageQueue.size() - lastMessageQueueSize.get() > PROCESSING_THRESHOLD
                ? PROCESSING_THRESHOLD
                : PROCESSING_THRESHOLD / 2;

        IntStream.range(0, processingCount)
                .mapToObj(i -> messageQueue.poll())
                .takeWhile(Objects::nonNull)
                .forEach(this::sendMessage);

        lastMessageQueueSize.set(messageQueue.size());
    }

    private void sendMessage(KafkaProducerRecord<String, String> message) {
        kafkaProducer.send(message)
                .onSuccess(recordMetadata -> log.info("Message sent: " + recordMetadata.toJson().encode()))
                .onFailure(error -> log.error("Could not send message of key: " + message.key()
                        + ", topic: " + message.topic() + " due to error: " + error.getMessage()));
    }
}
