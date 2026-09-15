package com.karan.url_shortener.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final int MAX_REQUESTS = 10;
    private static final long WINDOW_MINUTES = 1;

    private final StringRedisTemplate redisTemplate;

    public RateLimitInterceptor(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {

        String clientIp = getClientIp(request);
        String redisKey = "rate_limit:" + clientIp;

        Long requestCount =
                redisTemplate.opsForValue().increment(redisKey);

        // First request -> start expiry window
        if (requestCount != null && requestCount == 1) {
            redisTemplate.expire(
                    redisKey,
                    WINDOW_MINUTES,
                    TimeUnit.MINUTES
            );
        }

        // Limit exceeded
        if (requestCount != null && requestCount > MAX_REQUESTS) {

            response.setStatus(
                    HttpStatus.TOO_MANY_REQUESTS.value()
            );

            response.setContentType(
                    MediaType.APPLICATION_JSON_VALUE
            );

            response.setCharacterEncoding("UTF-8");

            response.setHeader("Retry-After", "60");

            String timestamp = LocalDateTime.now().toString();

            String jsonResponse = String.format(
                    "{\"status\":429," +
                            "\"message\":\"Too many requests. Please try again after 1 minute.\"," +
                            "\"timestamp\":\"%s\"}",
                    timestamp
            );

            response.getWriter().write(jsonResponse);

            return false;
        }

        return true;
    }

    private String getClientIp(HttpServletRequest request) {

        String forwardedFor =
                request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor
                    .split(",")[0]
                    .trim();
        }

        return request.getRemoteAddr();
    }
}