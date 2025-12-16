package com.volunteerhub.community.service.cache;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class SingleFlight {
    private final Map<String, CompletableFuture<?>> inFlight = new ConcurrentHashMap<>();
    private final Duration window;

    public SingleFlight(Duration window) {
        this.window = window;
    }

    @SuppressWarnings("unchecked")
    public <T> CompletableFuture<T> run(String key, Supplier<CompletableFuture<T>> supplier) {
        CompletableFuture<?> existing = inFlight.get(key);
        if (existing != null) {
            return (CompletableFuture<T>) existing;
        }

        CompletableFuture<T> created = supplier.get();
        inFlight.put(key, created);

        created.whenComplete((v, ex) -> scheduleRemoval(key));
        return created;
    }

    private void scheduleRemoval(String key) {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(window.toMillis());
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
            inFlight.remove(key);
        });
    }
}
