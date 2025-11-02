//package com.volunteerhub.community.permission;
//
//import com.volunteerhub.community.entity.*;
//import com.volunteerhub.community.entity.db_enum.TableType;
//import com.volunteerhub.community.repository.*;
//import lombok.AllArgsConstructor;
//
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//import java.util.UUID;
//
//@Component
//@AllArgsConstructor
//@Transactional(readOnly = true)
//public class PolicyContextRepository {
//    private UserProfileRepository userProfileRepository;
//    private final PostRepository postRepository;
//    private final CommentRepository commentRepository;
//    private final EventRepository eventRepository;
//    private final LikeRepository likeRepository;
//
//    public Optional<PolicyContext> getPolicyContext(UUID userId, Long targetId, String targetType) {
//        Long eventId = null;
//
//        UserProfile userProfile = userProfileRepository.findById(userId).orElse(null);
//
//        String systemRole =  userProfile != null ? userProfile.getRole().toString() : null;
//
//        if (TableType.POST.toString().equals(targetType)) {
//            Post post = postRepository.findById(targetId).orElse(null);
//            eventId = (post != null ? post.getEvent().getEventId() : null);
//        }
//
//        if (TableType.COMMENT.toString().equals(targetType)) {
//            Comment comment = commentRepository.findById(targetId).orElse(null);
//            eventId = (comment != null ? comment.getPost().getEvent().getEventId() : null);
//        }
//
//        if (TableType.EVENT.toString().equals(targetType)) {
//            eventId = targetId;
//        }
//
//        String eventRole = "EVENT_ADMIN";
//
//        PolicyContext policyContext = PolicyContext.builder()
//                .userId(userId.toString())
//                .eventId(eventId.toString())
//                .eventRole(eventRole)
//                .targetId(targetId.toString())
//                .targetType(targetType.toString())
//                .runtime(null)
//                .systemRole(systemRole)
//                .build();
//
//        return Optional.of(policyContext);
//    }
//}
