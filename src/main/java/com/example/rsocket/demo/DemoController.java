package com.example.rsocket.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Slf4j
@Controller
public class DemoController {
    @MessageMapping("send.string")
    public void sendString(Payload<String> payload) {
        log.info("Sending to topic: " + payload.topic());
        log.info("Data: " + payload.data());
    }

    @MessageMapping("subscribe.string")
    public Flux<String> subscribeString(String topic) {
        log.info("Subscribing to topic: " + topic);
        return Flux.just("First", "Second", "Third");
    }

    @MessageMapping("unsubscribe")
    public void unsubscribe() {
        log.info("Unsubscribing all");
    }
}
