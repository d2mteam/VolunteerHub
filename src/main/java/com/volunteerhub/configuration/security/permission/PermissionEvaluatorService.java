package com.volunteerhub.configuration.security.permission;

import com.volunteerhub.community.model.db_enum.EventState;
import com.volunteerhub.community.repository.EventRepository;
import com.volunteerhub.community.repository.PostRepository;
import com.volunteerhub.configuration.security.fga.FgaAuthorizationEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PermissionEvaluatorService {

    private static final Set<String> MANAGER_AUTHORITIES = Set.of("ADMIN", "EVENT_MANAGER");

    private final EventRepository eventRepository;
    private final PostRepository postRepository;
    private final FgaAuthorizationEngine fgaAuthorizationEngine;

    public void check(PermissionAction action, Long eventId, Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication is required");
        }

        UUID userId = extractUserId(authentication.getPrincipal());
        boolean isManager = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(MANAGER_AUTHORITIES::contains);

        Long resolvedEventId = resolveEventId(eventId, postId);
        EventState eventState = loadEventState(resolvedEventId);

        if ((action == PermissionAction.CREATE_POST || action == PermissionAction.CREATE_COMMENT)
                && eventState == EventState.PENDING) {
            throw new AccessDeniedException("Event is pending; posting is disabled");
        }

        if (isManager) {
            return;
        }

        FgaTarget target = resolveTarget(action, eventId, postId);
        boolean allowed = fgaAuthorizationEngine.check(userId, target.objectType(), target.objectId(), target.relation());
        if (!allowed) {
            throw new AccessDeniedException("User is not allowed to perform this action");
        }
    }

    private Long resolveEventId(Long eventId, Long postId) {
        if (eventId != null) {
            return eventId;
        }

        if (postId != null) {
            return postRepository.findEventIdByPostId(postId)
                    .orElseThrow(() -> new AccessDeniedException("Post not found"));
        }

        throw new AccessDeniedException("Event context is required");
    }

    private EventState loadEventState(Long eventId) {
        return eventRepository.findEventStateByEventId(eventId)
                .orElseThrow(() -> new AccessDeniedException("Event not found"));
    }

    private FgaTarget resolveTarget(PermissionAction action, Long eventId, Long postId) {
        return switch (action) {
            case GET_EVENT -> new FgaTarget("event", String.valueOf(resolveEventId(eventId, postId)), "viewer");
            case CREATE_POST -> new FgaTarget("event", String.valueOf(resolveEventId(eventId, postId)), "poster");
            case CREATE_COMMENT -> {
                if (postId == null) {
                    throw new AccessDeniedException("Post context is required");
                }
                yield new FgaTarget("post", String.valueOf(postId), "commenter");
            }
        };
    }

    private UUID extractUserId(Object principal) {
        if (principal instanceof UUID uuid) {
            return uuid;
        }
        if (principal instanceof String raw) {
            return UUID.fromString(raw);
        }
        throw new AccessDeniedException("Unsupported principal type");
    }

    private record FgaTarget(String objectType, String objectId, String relation) {}
}
