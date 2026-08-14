package com.juanperuzzo.flappynaruu.security;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RateLimiterTest {

    @Test
    public void shouldAllowUpToMaxRequestsWithinWindow() {
        RateLimiter limiter = new RateLimiter(3, 60_000);

        assertTrue(limiter.tryAcquire("ip-1"));
        assertTrue(limiter.tryAcquire("ip-1"));
        assertTrue(limiter.tryAcquire("ip-1"));
        assertFalse(limiter.tryAcquire("ip-1"));
    }

    @Test
    public void shouldTrackKeysIndependently() {
        RateLimiter limiter = new RateLimiter(1, 60_000);

        assertTrue(limiter.tryAcquire("ip-1"));
        assertTrue(limiter.tryAcquire("ip-2"));
        assertFalse(limiter.tryAcquire("ip-1"));
    }

    @Test
    public void shouldResetWindowAfterElapsedTime() {
        AtomicLong now = new AtomicLong(1_000_000L);
        RateLimiter limiter = new RateLimiter(1, 60_000, now::get);

        assertTrue(limiter.tryAcquire("ip-1"));
        assertFalse(limiter.tryAcquire("ip-1"));

        now.set(1_000_000L + 60_001L);

        assertTrue(limiter.tryAcquire("ip-1"));
    }
}