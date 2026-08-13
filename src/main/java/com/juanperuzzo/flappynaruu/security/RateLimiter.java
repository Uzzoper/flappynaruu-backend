package com.juanperuzzo.flappynaruu.security;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

/**
 * Fixed-window rate limiter keyed by an arbitrary string (e.g. client IP).
 * Thread-safe and dependency-free.
 */
public class RateLimiter {

    private static final int MAX_KEYS = 10_000;

    private final int maxRequests;
    private final long windowMillis;
    private final LongSupplier clock;
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    public RateLimiter(int maxRequests, long windowMillis) {
        this(maxRequests, windowMillis, System::currentTimeMillis);
    }

    RateLimiter(int maxRequests, long windowMillis, LongSupplier clock) {
        this.maxRequests = maxRequests;
        this.windowMillis = windowMillis;
        this.clock = clock;
    }

    public boolean tryAcquire(String key) {
        long now = clock.getAsLong();
        purgeStale(now);

        Window window = windows.compute(key, (k, existing) -> {
            if (existing == null || now - existing.start >= windowMillis) {
                return new Window(now, 1);
            }
            existing.count++;
            return existing;
        });
        return window.count <= maxRequests;
    }

    private void purgeStale(long now) {
        if (windows.size() > MAX_KEYS) {
            windows.entrySet().removeIf(e -> now - e.getValue().start >= windowMillis);
        }
    }

    private static final class Window {
        final long start;
        int count;

        Window(long start, int count) {
            this.start = start;
            this.count = count;
        }
    }
}