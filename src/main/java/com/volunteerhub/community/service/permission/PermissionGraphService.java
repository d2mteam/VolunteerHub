package com.volunteerhub.community.service.permission;

import com.volunteerhub.authentication.model.Role;
import com.volunteerhub.authentication.model.UserAuth;
import com.volunteerhub.authentication.repository.UserAuthRepository;
import com.volunteerhub.community.model.UserProfile;
import com.volunteerhub.community.model.permission.*;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.community.repository.permission.PermissionGrantRepository;
import com.volunteerhub.community.repository.permission.ResourceEdgeRepository;
import com.volunteerhub.community.repository.permission.ResourceNodeRepository;
import com.volunteerhub.ultis.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionGraphService {

    private final ResourceNodeRepository resourceNodeRepository;
    private final ResourceEdgeRepository resourceEdgeRepository;
    private final PermissionGrantRepository permissionGrantRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserAuthRepository userAuthRepository;
    private final SnowflakeIdGenerator idGenerator;

    @Transactional
    public ResourceNode registerRootResource(ResourceType type, Long objectId, UUID ownerId) {
        ResourceNode node = createNode(type, objectId, ownerId);
        grantOwnerAdmin(node, ownerId);
        grantRole(node, Role.USER.name(), Permission.VIEW);
        grantRole(node, Role.USER.name(), Permission.COMMENT);
        grantRole(node, Role.EVENT_MANAGER.name(), Permission.MODERATE);
        return node;
    }

    @Transactional
    public ResourceNode registerChildResource(ResourceType type, Long objectId, UUID ownerId, ResourceNode parent, Permission ownerPermission) {
        ResourceNode node = createNode(type, objectId, ownerId);
        link(parent, node);
        grantOwner(node, ownerId, ownerPermission);
        return node;
    }

    @Transactional(readOnly = true)
    public ResourceNode getNode(ResourceType type, Long objectId) {
        return resourceNodeRepository.findByTypeAndObjectId(type, objectId)
                .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + type + " " + objectId));
    }

    @Transactional(readOnly = true)
    public void assertPermission(UUID userId, ResourceType type, Long objectId, Permission required) {
        ResourceNode node = getNode(type, objectId);
        if (!hasPermission(userId, node.getResourceId(), required)) {
            throw new AccessDeniedException("User lacks permission " + required + " for " + type + " " + objectId);
        }
    }

    @Transactional(readOnly = true)
    public boolean hasPermission(UUID userId, Long resourceId, Permission required) {
        UserAuth userAuth = userAuthRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        List<String> effectivePermissions = permissionGrantRepository.findEffectivePermissions(
                resourceId,
                userId.toString(),
                Collections.singleton(userAuth.getRole().name())
        );

        Set<Permission> resolved = effectivePermissions.stream()
                .map(Permission::valueOf)
                .collect(Collectors.toSet());

        return includesRequiredPermission(resolved, required);
    }

    @Transactional
    public void softDelete(ResourceType type, Long objectId) {
        ResourceNode node = getNode(type, objectId);
        node.setStatus(ResourceStatus.SOFT_DELETED);
        resourceNodeRepository.save(node);
        permissionGrantRepository.findByResource_ResourceId(node.getResourceId()).forEach(grant -> {
            grant.setActive(false);
            permissionGrantRepository.save(grant);
        });
    }

    private ResourceNode createNode(ResourceType type, Long objectId, UUID ownerId) {
        UserProfile owner = userProfileRepository.getReferenceById(ownerId);
        ResourceNode node = ResourceNode.builder()
                .resourceId(idGenerator.nextId())
                .type(type)
                .objectId(objectId)
                .owner(owner)
                .status(ResourceStatus.ACTIVE)
                .build();
        return resourceNodeRepository.save(node);
    }

    private void link(ResourceNode parent, ResourceNode child) {
        ResourceEdge edge = ResourceEdge.builder()
                .edgeId(idGenerator.nextId())
                .parent(parent)
                .child(child)
                .build();
        resourceEdgeRepository.save(edge);
    }

    private void grantOwnerAdmin(ResourceNode node, UUID ownerId) {
        grantOwner(node, ownerId, Permission.ADMIN);
    }

    private void grantOwner(ResourceNode node, UUID ownerId, Permission permission) {
        PermissionGrant grant = PermissionGrant.builder()
                .grantId(idGenerator.nextId())
                .resource(node)
                .subjectType(SubjectType.USER)
                .subjectId(ownerId.toString())
                .permission(permission)
                .active(true)
                .build();
        permissionGrantRepository.save(grant);
    }

    private void grantRole(ResourceNode node, String roleName, Permission permission) {
        PermissionGrant grant = PermissionGrant.builder()
                .grantId(idGenerator.nextId())
                .resource(node)
                .subjectType(SubjectType.ROLE)
                .subjectId(roleName)
                .permission(permission)
                .active(true)
                .build();
        permissionGrantRepository.save(grant);
    }

    private boolean includesRequiredPermission(Set<Permission> resolved, Permission required) {
        if (resolved.contains(Permission.ADMIN)) {
            return true;
        }

        return switch (required) {
            case VIEW -> containsAny(resolved, EnumSet.of(Permission.VIEW, Permission.COMMENT, Permission.POST, Permission.MODERATE));
            case COMMENT -> containsAny(resolved, EnumSet.of(Permission.COMMENT, Permission.POST, Permission.MODERATE));
            case POST -> containsAny(resolved, EnumSet.of(Permission.POST, Permission.MODERATE));
            case MODERATE -> resolved.contains(Permission.MODERATE);
            case ADMIN -> resolved.contains(Permission.ADMIN);
        };
    }

    private boolean containsAny(Set<Permission> resolved, Set<Permission> accepted) {
        for (Permission permission : accepted) {
            if (resolved.contains(permission)) {
                return true;
            }
        }
        return false;
    }
}
