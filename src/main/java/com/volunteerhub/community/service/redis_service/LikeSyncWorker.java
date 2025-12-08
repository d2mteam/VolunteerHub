package com.volunteerhub.community.service.redis_service;

import com.volunteerhub.community.model.Like;
import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.repository.LikeRepository;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.ultis.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeSyncWorker {
    @Value("${redis.like-event}")
    private String redisLikeEvent;

    private final RedisTemplate<String, Object> redisTemplate;
    private final LikeRepository likeRepository; // DB
    private final UserProfileRepository userProfileRepository;
    private final SnowflakeIdGenerator idGenerator;

    @Scheduled(fixedDelay = 1000)  // mỗi giây kéo 1 batch
    @Transactional
    public void consume() {
        try {
            redisTemplate.opsForStream().createGroup(redisLikeEvent, ReadOffset.from("0"), "like_group");
        } catch (Exception e) {
            // ignore nếu group đã tồn tại
        }

        List<MapRecord<String,Object,Object>> records =
            redisTemplate.opsForStream().read(
                Consumer.from("like_group", "worker1"),
                StreamReadOptions.empty().count(200),
                StreamOffset.create(redisLikeEvent, ReadOffset.lastConsumed())
            );

        if (records == null) return;

        for (var record : records) {
            try {
                processRecord(record);
                redisTemplate.opsForStream().acknowledge(redisLikeEvent, "like_group", record.getId());
            } catch (Exception ex) {
                log.error("Failed to process like event {}", record.getId(), ex);
            }
        }
    }

    private void processRecord(MapRecord<String, Object, Object> record) {
        Map<Object, Object> values = record.getValue();
        String action = values.get("action").toString();
        TableType tableType = TableType.valueOf(values.get("tableType").toString());
        Long targetId = Long.valueOf(values.get("targetId").toString());
        UUID userId = UUID.fromString(values.get("userId").toString());

        Long likeId = values.containsKey("likeId")
                ? Long.parseLong(values.get("likeId").toString())
                : idGenerator.nextId();

        if (action.equals("LIKE")) {
            if (likeRepository.existsByCreatedBy_UserIdAndTargetIdAndTableType(userId, targetId, tableType)) {
                return;
            }

            Like like = Like.builder()
                    .likeId(likeId)
                    .targetId(targetId)
                    .tableType(tableType)
                    .createdBy(userProfileRepository.getReferenceById(userId))
                    .build();
            likeRepository.save(like);
        } else if (action.equals("UNLIKE")) {
            likeRepository.findByCreatedBy_UserIdAndTargetIdAndTableType(userId, targetId, tableType)
                    .ifPresent(likeRepository::delete);
        }
    }
}
