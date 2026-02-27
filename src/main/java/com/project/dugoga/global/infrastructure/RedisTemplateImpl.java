package com.project.dugoga.global.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisTemplateImpl implements RedisTemplate{
    private final org.springframework.data.redis.core.RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public <T> void write(String key, T value, Duration ttl) {
        try {
            String jsonString = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonString, ttl);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(key + " : " + value);
        }
    }

    @Override
    public <T> T read(String key, Class<T> type) {
        String jsonString = redisTemplate.opsForValue().get(key);

        if (jsonString == null) {
            return null;
        }

        try {
            return objectMapper.readValue(jsonString, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(key + " : " + type.getName());
        }
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
