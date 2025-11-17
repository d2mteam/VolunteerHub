package com.volunteerhub.ultis;

import lombok.Builder;

import java.util.concurrent.atomic.AtomicLong;

public class SnowflakeIdGenerator {

    private final long startEpoch;
    private final long workerId;
    private final long datacenterId;

    private final long sequenceMask;
    private final long workerIdShift;
    private final long datacenterIdShift;
    private final long timestampLeftShift;

    private final AtomicLong lastTimestamp = new AtomicLong(-1L);
    private final AtomicLong sequence = new AtomicLong(0L);

    @Builder
    public SnowflakeIdGenerator(
            long workerIdBits,
            long datacenterIdBits,
            long sequenceBits,
            long workerId,
            long datacenterId,
            long startEpoch
    ) {
        this.startEpoch = startEpoch;
        this.workerId = workerId;
        this.datacenterId = datacenterId;

        long maxDatacenterId = (1L << datacenterIdBits) - 1;
        long maxWorkerId = (1L << workerIdBits) - 1;
        this.sequenceMask = (1L << sequenceBits) - 1;

        this.workerIdShift = sequenceBits;
        this.datacenterIdShift = sequenceBits + workerIdBits;
        this.timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

        if (workerId > maxWorkerId || workerId < 0)
            throw new IllegalArgumentException("workerId out of range (0-" + maxWorkerId + ")");
        if (datacenterId > maxDatacenterId || datacenterId < 0)
            throw new IllegalArgumentException("datacenterId out of range (0-" + maxDatacenterId + ")");
    }

    public long nextId() {
        while (true) {
            long current = System.currentTimeMillis();
            long last = lastTimestamp.get();

            if (current < last) {
                throw new RuntimeException("Clock moved backwards.");
            }

            if (current == last) {
                long seq = (sequence.incrementAndGet()) & sequenceMask;
                if (seq == 0) {
                    current = waitNextMillis(last);
                } else {
                    return makeId(current, seq);
                }
            } else {
                if (lastTimestamp.compareAndSet(last, current)) {
                    sequence.set(0);
                    return makeId(current, 0);
                }
            }
        }
    }

    private long makeId(long timestamp, long seq) {
        return ((timestamp - startEpoch) << timestampLeftShift)
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift)
                | seq;
    }

    private long waitNextMillis(long lastTimestamp) {
        long ts = System.currentTimeMillis();
        while (ts <= lastTimestamp) {
            ts = System.currentTimeMillis();
        }
        return ts;
    }
}
