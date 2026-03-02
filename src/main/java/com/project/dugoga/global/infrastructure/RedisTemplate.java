package com.project.dugoga.global.infrastructure;

import java.time.Duration;

public interface RedisTemplate {
    <T> void write(String key, T value, Duration ttl);

    <T> T read(String key, Class<T> type);

    void delete(String key);
}
