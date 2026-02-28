package com.project.dugoga.global.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisTemplateImpl implements RedisTemplate{
    private final org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate;

    @Override
    public <T> void write(String key, T value, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl);
        } catch (DataAccessException e) {
            throw new RuntimeException(key + " : " + value);
        }
    }

    @Override
    public <T> T read(String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return null;
        }

        try {
            return type.cast(value);
        } catch (DataAccessException e) {
            throw new RuntimeException(key + " : " + type.getName());
        }
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
