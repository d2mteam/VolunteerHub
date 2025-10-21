package com.volunteerhub.ultis;

import java.util.UUID;

public class IDGenerator {
    public static long generatorID() {
        return UUID.randomUUID().getLeastSignificantBits();
    }
}
