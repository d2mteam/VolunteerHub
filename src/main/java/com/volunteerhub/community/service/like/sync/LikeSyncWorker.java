package com.volunteerhub.community.service.like.sync;

import com.volunteerhub.community.model.entity.Like;
import com.volunteerhub.community.model.entity.UserProfile;
import com.volunteerhub.community.repository.LikeRepository;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.community.service.like.gateway.RedisLikeGateway;
import com.volunteerhub.community.service.like.model.LikeEventPayload;
import com.volunteerhub.ultis.SnowflakeIdGenerator;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LikeSyncWorker {
    private static final Logger log = LoggerFactory.getLogger(LikeSyncWorker.class);
    private static final String CONSUMER_GROUP = "like-sync-group";

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisLikeGateway redisLikeGateway;
    private final LikeRepository likeRepository;
    private final UserProfileRepository userProfileRepository;
    private final SnowflakeIdGenerator idGenerator;

    public LikeSyncWorker(RedisTemplate<String, Object> redisTemplate,
                          RedisLikeGateway redisLikeGateway,
                          LikeRepository likeRepository,
                          UserProfileRepository userProfileRepository,
                          SnowflakeIdGenerator idGenerator) {
        this.redisTemplate = redisTemplate;
        this.redisLikeGateway = redisLikeGateway;
        this.likeRepository = likeRepository;
        this.userProfileRepository = userProfileRepository;
        this.idGenerator = idGenerator;
    }

    @PostConstruct
    public void ensureGroup() {
        try {
            redisTemplate.opsForStream().createGroup(redisLikeGateway.getStreamKey(), ReadOffset.from("0"), CONSUMER_GROUP);
        } catch (Exception e) {
            log.debug("Stream group may already exist: {}", e.getMessage());
        }
    }

    @Scheduled(fixedDelayString = "PT5S")
    @Transactional
    public void sync() {
        List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream().read(
                StreamOffset.create(redisLikeGateway.getStreamKey(), ReadOffset.lastConsumed()));
        if (records == null || records.isEmpty()) {
            return;
        }
        for (MapRecord<String, Object, Object> record : records) {
            Map<Object, Object> value = record.getValue();
            LikeEventPayload payload = LikeEventPayload.fromRecord(value.entrySet().stream()
                    .collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue)));
            if ("LIKE".equalsIgnoreCase(payload.getAction())) {
                handleLike(payload);
            } else if ("UNLIKE".equalsIgnoreCase(payload.getAction())) {
                handleUnlike(payload);
            }
            redisTemplate.opsForStream().delete(redisLikeGateway.getStreamKey(), record.getId());
        }
    }

    private void handleLike(LikeEventPayload payload) {
        Optional<Like> existing = likeRepository.findByCreatedBy_UserIdAndTargetIdAndTableType(
                payload.getUserId(), payload.getTargetId(), payload.getTableType());
        if (existing.isPresent()) {
            return;
        }
        Optional<UserProfile> user = userProfileRepository.findById(payload.getUserId());
        if (user.isEmpty()) {
            log.warn("Cannot sync like without user {}", payload.getUserId());
            return;
        }
        Like like = Like.builder()
                .likeId(idGenerator.nextId())
                .targetId(payload.getTargetId())
                .tableType(payload.getTableType())
                .createdBy(user.get())
                .build();
        likeRepository.save(like);
    }

    private void handleUnlike(LikeEventPayload payload) {
        Optional<Like> existing = likeRepository.findByCreatedBy_UserIdAndTargetIdAndTableType(
                payload.getUserId(), payload.getTargetId(), payload.getTableType());
        existing.ifPresent(likeRepository::delete);
    }
}
