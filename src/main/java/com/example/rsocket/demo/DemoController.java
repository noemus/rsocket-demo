package com.example.rsocket.demo;

import com.google.protobuf.InvalidProtocolBufferException;
import example.Example;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.Base64;

@Slf4j
@Controller
public class DemoController {
    @MessageMapping("send.string")
    public void sendString(Payload<String> payload) {
        log.info("Sending to topic: " + payload.topic());
        log.info("Data: " + payload.data());
    }

    @MessageMapping("send.bytes")
    public void sendBytes(BytesPayload payload) {
        log.info("Sending to topic: " + payload.topic());
        log.info("Data: " + payload.encodedData());

        try {
            var request = Example.SearchRequest.parseFrom(payload.bytes());
            log.info("SearchRequest: " + request.toString());
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

    @MessageMapping("subscribe.string")
    public Flux<String> subscribeString(String topic) {
        log.info("Subscribing to topic: " + topic);
        return Flux.just("First", "Second", "Third");
    }

    @MessageMapping("subscribe.bytes")
    public Flux<String> subscribeBytes(String topic) {
        log.info("Subscribing to topic: " + topic);
        var searchRequest = Example.SearchRequest.newBuilder().setQuery("Hello!").build();
        var bytes = searchRequest.toByteArray();
        var base64 = Base64.getEncoder().encodeToString(bytes);
        return Flux.just(base64);
    }

    @MessageMapping("unsubscribe")
    public void unsubscribe() {
        log.info("Unsubscribing all");
    }
}
