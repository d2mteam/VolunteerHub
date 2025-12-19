package com.volunteerhub.community.service.like.model;

import com.volunteerhub.community.model.db_enum.TableType;

import java.util.Objects;

public final class LikeTargetKey {
    private final TableType tableType;
    private final Long targetId;

    public LikeTargetKey(TableType tableType, Long targetId) {
        this.tableType = tableType;
        this.targetId = targetId;
    }

    public TableType getTableType() {
        return tableType;
    }

    public Long getTargetId() {
        return targetId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LikeTargetKey that = (LikeTargetKey) o;
        return tableType == that.tableType && Objects.equals(targetId, that.targetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableType, targetId);
    }

    @Override
    public String toString() {
        return "LikeTargetKey{" +
                "tableType=" + tableType +
                ", targetId=" + targetId +
                '}';
    }
}
