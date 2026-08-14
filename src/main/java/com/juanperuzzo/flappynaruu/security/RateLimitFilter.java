package com.juanperuzzo.flappynaruu.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

/**
 * Limits score submissions per client IP to prevent spam and abuse.
 * Only applies to POST /leaderboard. Responds with 429 when the limit is exceeded.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";

    private final RateLimiter rateLimiter;
    private final ObjectMapper objectMapper;

    public RateLimitFilter(@Value("${RATE_LIMIT_MAX_REQUESTS:10}") int maxRequests,
                           @Value("${RATE_LIMIT_WINDOW_SECONDS:60}") long windowSeconds,
                           ObjectMapper objectMapper) {
        this.rateLimiter = new RateLimiter(maxRequests, windowSeconds * 1000);
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !("POST".equals(request.getMethod()) && "/leaderboard".equals(request.getRequestURI()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!rateLimiter.tryAcquire(clientIp(request))) {
            response.setStatus(429);
            response.setContentType("application/json");
            objectMapper.writeValue(response.getWriter(), Map.of(
                    "timestamp", Instant.now(),
                    "error", "RATE_LIMITED",
                    "message", "Too many requests. Try again later."));
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader(X_FORWARDED_FOR);
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}