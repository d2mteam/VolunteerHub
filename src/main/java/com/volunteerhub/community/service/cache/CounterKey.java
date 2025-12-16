package com.volunteerhub.community.service.cache;

import com.volunteerhub.community.model.db_enum.TableType;

public record CounterKey(TableType targetType, Long targetId) {
    @Override
    public String toString() {
        return targetType.name() + ":" + targetId;
    }
}
