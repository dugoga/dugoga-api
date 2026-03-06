package com.project.dugoga.global.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class StringRedisTemplateImpl implements StringRedisTemplate{
    private final org.springframework.data.redis.core.StringRedisTemplate stringRedisTemplate;

    public void write(String key, String value, Duration ttl) {
        try{
            stringRedisTemplate.opsForValue().set(key, value, ttl);
        } catch (DataAccessException e){
            throw new RuntimeException(key + " : " + value, e);
        }
    }

    public String read(String key) {
        try{
            return stringRedisTemplate.opsForValue().get(key);
        } catch(DataAccessException e){
            throw new RuntimeException("key : " + key, e);
        }
    }

    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }
}
