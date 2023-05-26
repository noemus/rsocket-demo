package com.example.rsocket.demo;

import io.rsocket.RSocket;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.rsocket.server.LocalRSocketServerPort;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DemoApplicationTests {

    private static RSocketRequester requester;

    @BeforeAll
    static void setup(
		@Autowired RSocketRequester.Builder builder,
		@LocalRSocketServerPort Integer port
    ) {
        requester = builder.tcp("localhost", port);
    }

    @AfterAll
    static void cleanup() {
        Optional.ofNullable(requester.rsocket())
                .ifPresent(RSocket::dispose);
    }

    @Test
    void send_string() {
        requester.route("send.string")
                 .data(new Payload<>("send/to/topic", "Hello RSocket"))
                 .send()
                 .block(Duration.ofMillis(500));
    }

    @Test
    void subscribe_strings() {
        var strings = requester.route("subscribe.string")
                               .data("subscribe/from/topic")
                               .retrieveFlux(String.class);

        StepVerifier.create(strings)
                    .thenRequest(3)
                    .assertNext(str -> assertEquals("First", str))
                    .assertNext(str -> assertEquals("Second", str))
                    .assertNext(str -> assertEquals("Third", str))
                    .verifyComplete();
    }

    @Test
    void unsubscribe() {
        requester.route("unsubscribe")
                 .send()
                 .block();
    }
}
