package com.karan.url_shortener.interceptor;

import com.karan.url_shortener.service.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    private final RateLimitService rateLimitService;
    public RateLimitInterceptor(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        String ipAddress = request.getRemoteAddr();

        if(!rateLimitService.isAllowed(ipAddress)) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write(
                    """
                     {
                     "Status" : 429,
                     "message" : "Too many requests.
                                 please try after 1 minute.",
                        "timestamp" : "%s"         
                     }
                     """.formatted(java.time.LocalDateTime.now())
            );
            return false;
        }
        return true;
    }
}
