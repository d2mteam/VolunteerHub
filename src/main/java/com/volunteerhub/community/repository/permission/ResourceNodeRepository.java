package com.volunteerhub.community.repository.permission;

import com.volunteerhub.community.model.permission.ResourceNode;
import com.volunteerhub.community.model.permission.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResourceNodeRepository extends JpaRepository<ResourceNode, Long> {
    Optional<ResourceNode> findByTypeAndObjectId(ResourceType type, Long objectId);
}
