package com.volunteerhub.configuration.security.fga;

import com.volunteerhub.community.model.db_enum.EventRole;
import com.volunteerhub.community.model.db_enum.ParticipationStatus;
import com.volunteerhub.community.repository.CommentRepository;
import com.volunteerhub.community.repository.EventRepository;
import com.volunteerhub.community.repository.PostRepository;
import com.volunteerhub.community.repository.RoleInEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CommunityFgaTupleResolver implements FgaTupleResolver {

    private static final Set<ParticipationStatus> ACTIVE_STATUSES =
            EnumSet.of(ParticipationStatus.APPROVED, ParticipationStatus.COMPLETED);

    private final EventRepository eventRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final RoleInEventRepository roleInEventRepository;

    @Override
    public boolean hasUserRelation(String objectType, String objectId, String relation, UUID userId) {
        return switch (objectType) {
            case "event" -> checkEventRelation(Long.valueOf(objectId), relation, userId);
            case "post" -> checkPostRelation(Long.valueOf(objectId), relation, userId);
            case "comment" -> checkCommentRelation(Long.valueOf(objectId), relation, userId);
            case "user" -> userId.toString().equals(objectId) && relation.equals("self");
            default -> false;
        };
    }

    @Override
    public Optional<FgaObjectRef> resolveObjectRelation(String objectType, String objectId, String relation) {
        return switch (objectType) {
            case "post" -> resolvePostRelation(Long.valueOf(objectId), relation);
            case "comment" -> resolveCommentRelation(Long.valueOf(objectId), relation);
            default -> Optional.empty();
        };
    }

    private boolean checkEventRelation(Long eventId, String relation, UUID userId) {
        return switch (relation) {
            case "owner" -> eventRepository.findCreatedByUserId(eventId)
                    .map(userId::equals)
                    .orElse(false);
            case "member" -> roleInEventRepository
                    .existsByUserProfile_UserIdAndEvent_EventIdAndParticipationStatusIn(
                            userId,
                            eventId,
                            ACTIVE_STATUSES
                    );
            case "admin" -> roleInEventRepository
                    .existsByUserProfile_UserIdAndEvent_EventIdAndEventRoleAndParticipationStatusIn(
                            userId,
                            eventId,
                            EventRole.EVENT_ADMIN,
                            ACTIVE_STATUSES
                    );
            default -> false;
        };
    }

    private boolean checkPostRelation(Long postId, String relation, UUID userId) {
        if (!relation.equals("owner")) {
            return false;
        }
        return postRepository.findCreatedByUserId(postId)
                .map(userId::equals)
                .orElse(false);
    }

    private boolean checkCommentRelation(Long commentId, String relation, UUID userId) {
        if (!relation.equals("owner")) {
            return false;
        }
        return commentRepository.findCreatedByUserId(commentId)
                .map(userId::equals)
                .orElse(false);
    }

    private Optional<FgaObjectRef> resolvePostRelation(Long postId, String relation) {
        if (!relation.equals("event")) {
            return Optional.empty();
        }
        return postRepository.findEventIdByPostId(postId)
                .map(eventId -> new FgaObjectRef("event", eventId.toString()));
    }

    private Optional<FgaObjectRef> resolveCommentRelation(Long commentId, String relation) {
        if (!relation.equals("post")) {
            return Optional.empty();
        }
        return commentRepository.findPostIdByCommentId(commentId)
                .map(postId -> new FgaObjectRef("post", postId.toString()));
    }
}
