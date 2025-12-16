package com.volunteerhub.community.service.cache;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SimpleCircuitBreaker {
    private final int failureThreshold;
    private final Duration openDuration;
    private final AtomicInteger consecutiveFailures = new AtomicInteger(0);
    private final AtomicLong openUntilMillis = new AtomicLong(0);

    public SimpleCircuitBreaker(int failureThreshold, Duration openDuration) {
        this.failureThreshold = failureThreshold;
        this.openDuration = openDuration;
    }

    public boolean allowRequest() {
        long now = System.currentTimeMillis();
        long until = openUntilMillis.get();
        if (until == 0 || now > until) {
            return true;
        }
        return false;
    }

    public boolean isOpen() {
        long until = openUntilMillis.get();
        return until != 0 && System.currentTimeMillis() < until;
    }

    public void recordSuccess() {
        consecutiveFailures.set(0);
        openUntilMillis.set(0);
    }

    public void recordFailure() {
        if (consecutiveFailures.incrementAndGet() >= failureThreshold) {
            openUntilMillis.set(Instant.now().plus(openDuration).toEpochMilli());
        }
    }
}
