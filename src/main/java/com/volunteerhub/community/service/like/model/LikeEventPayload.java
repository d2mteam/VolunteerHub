package com.volunteerhub.community.service.like.model;

import com.volunteerhub.community.model.db_enum.TableType;

import java.util.Map;
import java.util.UUID;

public class LikeEventPayload {
    private final String action;
    private final TableType tableType;
    private final Long targetId;
    private final UUID userId;

    public LikeEventPayload(String action, TableType tableType, Long targetId, UUID userId) {
        this.action = action;
        this.tableType = tableType;
        this.targetId = targetId;
        this.userId = userId;
    }

    public String getAction() {
        return action;
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

    public static LikeEventPayload fromRecord(Map<String, Object> value) {
        String action = (String) value.get("action");
        TableType tableType = TableType.valueOf((String) value.get("tableType"));
        Long targetId = Long.valueOf(value.get("targetId").toString());
        UUID userId = UUID.fromString(value.get("userId").toString());
        return new LikeEventPayload(action, tableType, targetId, userId);
    }
}
