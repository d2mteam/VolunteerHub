package com.volunteerhub.community.service.write_service.impl;

import com.volunteerhub.community.dto.ActionResponse;
import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.service.redis_service.RedisLikeService;
import com.volunteerhub.community.service.permission.PermissionGraphService;
import com.volunteerhub.community.service.write_service.ILikeService;
import com.volunteerhub.ultis.SnowflakeIdGenerator;
import com.volunteerhub.community.model.permission.Permission;
import com.volunteerhub.community.model.permission.ResourceNode;
import com.volunteerhub.community.model.permission.ResourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeService implements ILikeService {

    private final RedisLikeService redisLikeService;
    private final PermissionGraphService permissionGraphService;
    private final SnowflakeIdGenerator idGenerator;

    @Override
    public ActionResponse<Void> like(UUID userId, Long targetId, String targetType) {
        TableType tableType = TableType.valueOf(targetType); // validate enum, sẽ throw nếu sai
        ResourceType resourceType = mapResourceType(tableType);
        permissionGraphService.assertPermission(userId, resourceType, targetId, Permission.VIEW);

        Long likeId = idGenerator.nextId();
        redisLikeService.like(targetId, targetType, userId, likeId);

        ResourceNode parentNode = permissionGraphService.getNode(resourceType, targetId);
        permissionGraphService.registerChildResource(ResourceType.LIKE, likeId, userId, parentNode, Permission.MODERATE);
        LocalDateTime now = LocalDateTime.now();
        return ActionResponse.success(
                likeId.toString(),
                now,
                now
        );
    }

    @Override
    public ActionResponse<Void> unlike(UUID userId, Long targetId, String targetType) {
        TableType tableType = TableType.valueOf(targetType); // validate enum

        Long likeId = redisLikeService.unlike(targetId, targetType, userId);
        if (likeId != null) {
            permissionGraphService.assertPermission(userId, ResourceType.LIKE, likeId, Permission.MODERATE);
            permissionGraphService.softDelete(ResourceType.LIKE, likeId);
        }
        LocalDateTime now = LocalDateTime.now();
        return ActionResponse.success(
                likeId != null ? likeId.toString() : targetId.toString(),
                now,
                now
        );
    }

    private ResourceType mapResourceType(TableType tableType) {
        return switch (tableType) {
            case EVENT -> ResourceType.EVENT;
            case POST -> ResourceType.POST;
            case COMMENT -> ResourceType.COMMENT;
            case LIKE -> ResourceType.LIKE;
        };
    }
}
