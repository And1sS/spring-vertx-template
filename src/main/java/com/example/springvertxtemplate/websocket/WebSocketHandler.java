package com.example.springvertxtemplate.websocket;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.ServerWebSocket;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WebSocketHandler implements Handler<ServerWebSocket> {

    @NonNull
    EventBus eventBus;

    @Override
    public void handle(ServerWebSocket webSocketConnection) {
        MessageConsumer<String> messageConsumer = eventBus.<String>localConsumer("/userId")
                .handler(message -> webSocketConnection.writeTextMessage(message.body()));
        webSocketConnection.textMessageHandler(message -> eventBus.publish("/userId", message));

        webSocketConnection.closeHandler(event -> messageConsumer.unregister());
    }
}