package com.project.dugoga.global.infrastructure;

import java.time.Duration;

public interface StringRedisTemplate {
    void write(String key, String value, Duration ttl);

    String read(String key);

    void delete(String key);
}
