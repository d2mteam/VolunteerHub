package com.volunteerhub.community.service.redis_service;

import com.volunteerhub.community.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LikeSyncWorker {
    @Value("${redis.like-event}")
    private String redisLikeEvent;

    private final RedisTemplate<String, Object> redisTemplate;
    private final LikeRepository likeRepository; // DB

    @Scheduled(fixedDelay = 1000)  // mỗi giây kéo 1 batch
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
            var values = record.getValue();
            String action = values.get("action").toString();
            String tableType = values.get("tableType").toString();
            Long targetId = Long.valueOf(values.get("targetId").toString());
            UUID userId = UUID.fromString(values.get("userId").toString());

            if (action.equals("LIKE")) {
//                Like like = Like.builder()
//                        .targetId(targetId)
//                        .tableType(TableType.valueOf(tableType))
//                        .createdBy(null)
//                        .build();
//                likeRepository.saveIfNotExists(userId, tableType, targetId);
            } else {
//                likeRepository.delete(userId, tableType, targetId);
            }

            // ACK event
            redisTemplate.opsForStream().acknowledge("like_events", "like_group", record.getId());
        }
    }
}
