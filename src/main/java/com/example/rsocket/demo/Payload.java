package com.example.rsocket.demo;

import java.util.Arrays;

public record Payload<T>(String topic, T data) {
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Payload<?> other) {
            if (!topic.equals(other.topic)) {
                return false;
            }
            if (data instanceof byte[] bytes && other.data instanceof byte[] otherBytes) {
                return Arrays.equals(bytes, otherBytes);
            }
        }
        return false;
    }
}
