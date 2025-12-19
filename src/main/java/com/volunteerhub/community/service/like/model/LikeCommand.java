package com.volunteerhub.community.service.like.model;

import com.volunteerhub.community.model.db_enum.TableType;

import java.util.UUID;

public class LikeCommand {
    private final TableType tableType;
    private final Long targetId;
    private final UUID userId;

    public LikeCommand(TableType tableType, Long targetId, UUID userId) {
        this.tableType = tableType;
        this.targetId = targetId;
        this.userId = userId;
    }

    public TableType getTableType() {
        return tableType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public UUID getUserId() {
        return userId;
    }
}
