package com.example.rsocket.demo;

import java.util.Base64;

public record BytesPayload(String topic, String encodedData) {
    public byte[] bytes() {
        return Base64.getDecoder().decode(encodedData);
    }

    public static BytesPayload create(String topic, byte[] bytes) {
        return new BytesPayload(topic, Base64.getEncoder().encodeToString(bytes));
    }
}
