package com.karan.url_shortener.service;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimitService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final int MAX_REQUESTS = 10;
    private static final Duration WINDOW = Duration.ofMinutes(1);

    public RateLimitService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(String ipAddress) {
        String key = "rate_limit :" + ipAddress;

        String current = redisTemplate.opsForValue().get(key);
        if(current == null) {
            redisTemplate.opsForValue().set(key, "1" , WINDOW);
            return true;
        }
        int count = Integer.parseInt(current);
        if(count >= MAX_REQUESTS){
            return false;
        }

        redisTemplate.opsForValue().increment(key);
        return true;
    }

}
