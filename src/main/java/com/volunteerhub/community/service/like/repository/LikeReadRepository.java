package com.volunteerhub.community.service.like.repository;

import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.model.entity.Like;
import com.volunteerhub.community.service.like.model.LikeTargetKey;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class LikeReadRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Map<LikeTargetKey, Boolean> fetchMembership(UUID userId, List<LikeTargetKey> targets) {
        if (targets.isEmpty()) {
            return Map.of();
        }
        Map<LikeTargetKey, Boolean> result = new HashMap<>();
        Map<TableType, List<Long>> groupedTargets = targets.stream()
                .collect(Collectors.groupingBy(LikeTargetKey::getTableType, Collectors.mapping(LikeTargetKey::getTargetId, Collectors.toList())));

        groupedTargets.forEach((tableType, ids) -> {
            List<Long> existing = entityManager.createQuery(
                            "select l.targetId from Like l where l.createdBy.userId = :userId and l.tableType = :tableType and l.targetId in :targetIds",
                            Long.class)
                    .setParameter("userId", userId)
                    .setParameter("tableType", tableType)
                    .setParameter("targetIds", ids)
                    .getResultList();
            existing.forEach(targetId -> result.put(new LikeTargetKey(tableType, targetId), true));
        });
        return result;
    }

    public Map<Long, Long> countByTargets(TableType tableType, Collection<Long> targetIds) {
        if (targetIds.isEmpty()) {
            return Map.of();
        }
        List<Tuple> tuples = entityManager.createQuery(
                        "select l.targetId as targetId, count(l) as cnt from Like l where l.tableType = :tableType and l.targetId in :targetIds group by l.targetId",
                        Tuple.class)
                .setParameter("tableType", tableType)
                .setParameter("targetIds", targetIds)
                .getResultList();
        Map<Long, Long> result = new HashMap<>();
        tuples.forEach(tuple -> result.put(tuple.get("targetId", Long.class), tuple.get("cnt", Long.class)));
        return result;
    }
}
