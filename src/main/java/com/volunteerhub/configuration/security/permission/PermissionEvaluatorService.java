package com.volunteerhub.configuration.security.permission;

import com.volunteerhub.community.model.db_enum.EventState;
import com.volunteerhub.community.model.db_enum.ParticipationStatus;
import com.volunteerhub.community.repository.EventRepository;
import com.volunteerhub.community.repository.PostRepository;
import com.volunteerhub.community.repository.RoleInEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PermissionEvaluatorService {

    private static final Set<ParticipationStatus> ACTIVE_STATUSES =
            EnumSet.of(ParticipationStatus.APPROVED, ParticipationStatus.COMPLETED);
    private static final Set<String> MANAGER_AUTHORITIES = Set.of("ADMIN", "EVENT_MANAGER");

    private final EventRepository eventRepository;
    private final RoleInEventRepository roleInEventRepository;
    private final PostRepository postRepository;

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

        if (!isManager && requiresMembership(action)) {
            ParticipationStatus status = findParticipationStatus(userId, resolvedEventId)
                    .orElseThrow(() -> new AccessDeniedException("User is not a member of this event"));

            if (!ACTIVE_STATUSES.contains(status)) {
                throw new AccessDeniedException("User is not approved to access this event");
            }
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

    private Optional<ParticipationStatus> findParticipationStatus(UUID userId, Long eventId) {
        return roleInEventRepository.findParticipationStatus(userId, eventId);
    }

    private boolean requiresMembership(PermissionAction action) {
        return action == PermissionAction.GET_EVENT
                || action == PermissionAction.CREATE_POST
                || action == PermissionAction.CREATE_COMMENT;
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
}
