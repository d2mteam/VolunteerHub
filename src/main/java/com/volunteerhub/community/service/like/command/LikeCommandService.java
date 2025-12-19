package com.volunteerhub.community.service.like.command;

import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.service.like.gateway.RedisLikeGateway;
import com.volunteerhub.community.service.like.model.LikeCommand;
import com.volunteerhub.community.service.like.model.LikeCommandResult;
import org.springframework.stereotype.Service;

@Service
public class LikeCommandService {

    private final RedisLikeGateway redisLikeGateway;

    public LikeCommandService(RedisLikeGateway redisLikeGateway) {
        this.redisLikeGateway = redisLikeGateway;
    }

    public LikeCommandResult like(LikeCommand command) {
        RedisLikeGateway.LikeGatewayResult result = redisLikeGateway.like(command.getTableType(), command.getTargetId(), command.getUserId());
        return new LikeCommandResult(result.applied(), result.likeCount());
    }

    public LikeCommandResult unlike(LikeCommand command) {
        RedisLikeGateway.LikeGatewayResult result = redisLikeGateway.unlike(command.getTableType(), command.getTargetId(), command.getUserId());
        return new LikeCommandResult(result.applied(), result.likeCount());
    }

    public LikeCommandResult toggle(boolean like, TableType tableType, Long targetId, java.util.UUID userId) {
        LikeCommand command = new LikeCommand(tableType, targetId, userId);
        return like ? like(command) : unlike(command);
    }
}
