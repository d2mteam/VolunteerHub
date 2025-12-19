package com.volunteerhub.community.service.write_service.impl;

import com.volunteerhub.community.dto.rest.response.*;
import com.volunteerhub.community.model.db_enum.ParticipationStatus;
import com.volunteerhub.community.model.db_enum.RegistrationStatus;
import com.volunteerhub.community.model.entity.Event;
import com.volunteerhub.community.model.entity.EventRegistration;
import com.volunteerhub.community.model.entity.RoleInEvent;
import com.volunteerhub.community.model.entity.UserProfile;
import com.volunteerhub.community.repository.EventRegistrationRepository;
import com.volunteerhub.community.repository.EventRepository;
import com.volunteerhub.community.repository.RoleInEventRepository;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.community.service.write_service.IEventRegistrationService;
import com.volunteerhub.ultis.SnowflakeIdGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class EventRegistrationService implements IEventRegistrationService {

    private static final Set<ParticipationStatus> ACTIVE_STATES =
            Set.of(ParticipationStatus.APPROVED, ParticipationStatus.COMPLETED);

    private final RoleInEventRepository roleInEventRepository;
    private final EventRegistrationRepository eventRegistrationRepository;
    private final EventRepository eventRepository;
    private final UserProfileRepository userProfileRepository;
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    // ========================= REGISTER =========================

    @Override
    public ModerationResponse registerEvent(UUID userId, Long eventId) {

        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            return failure(ModerationAction.REGISTER_EVENT, eventId, "EVENT_NOT_FOUND");
        }

        RoleInEvent roleInEvent = roleInEventRepository
                .findByUserProfile_UserIdAndEvent_EventId(userId, eventId)
                .orElse(null);

        if (roleInEvent != null) {
            if (ACTIVE_STATES.contains(roleInEvent.getParticipationStatus())
                    || roleInEvent.getParticipationStatus() == ParticipationStatus.PENDING) {
                return failure(
                        ModerationAction.REGISTER_EVENT,
                        eventId,
                        "USER_ALREADY_REGISTERED"
                );
            }

            roleInEvent.setParticipationStatus(ParticipationStatus.PENDING);
            roleInEventRepository.save(roleInEvent);
        } else {
            UserProfile user = userProfileRepository.getReferenceById(userId);

            roleInEvent = RoleInEvent.builder()
                    .id(snowflakeIdGenerator.nextId())
                    .event(event)
                    .userProfile(user)
                    .participationStatus(ParticipationStatus.PENDING)
                    .build();

            roleInEventRepository.save(roleInEvent);
        }


        Long roleInEventId = roleInEvent.getId();
        // ===== sync EventRegistration (READ MODEL) =====
        eventRegistrationRepository
                .findByUserIdAndEventIdAndStatus(
                        userId, eventId, RegistrationStatus.PENDING
                )
                .orElseGet(() -> {
                    EventRegistration reg = EventRegistration.builder()
                            .registrationId(roleInEventId)
                            .userId(userId)
                            .eventId(eventId)
                            .status(RegistrationStatus.PENDING)
                            .build();
                    return eventRegistrationRepository.save(reg);
                });

        return ModerationResponse.success(
                ModerationAction.REGISTER_EVENT,
                ModerationTargetType.EVENT,
                roleInEvent.getId().toString(),
                ModerationStatus.REGISTERED,
                "Registration created"
        );
    }

    // ========================= APPROVE =========================

    @Override
    public ModerationResponse approveRegistration(Long roleInEventId) {

        RoleInEvent roleInEvent = roleInEventRepository.findById(roleInEventId).orElse(null);
        if (roleInEvent == null) {
            return failure(
                    ModerationAction.APPROVE_REGISTRATION,
                    roleInEventId,
                    "ROLE_NOT_FOUND"
            );
        }

        if (roleInEvent.getParticipationStatus() != ParticipationStatus.PENDING) {
            return failure(
                    ModerationAction.APPROVE_REGISTRATION,
                    roleInEventId,
                    "ROLE_ALREADY_PROCESSED"
            );
        }

        roleInEvent.setParticipationStatus(ParticipationStatus.APPROVED);
        roleInEventRepository.save(roleInEvent);

        // ===== sync EventRegistration =====
        eventRegistrationRepository
                .findByUserIdAndEventIdAndStatus(
                        roleInEvent.getUserProfile().getUserId(),
                        roleInEvent.getEvent().getEventId(),
                        RegistrationStatus.PENDING
                )
                .ifPresent(reg -> {
                    reg.setStatus(RegistrationStatus.APPROVED);
                    eventRegistrationRepository.save(reg);
                });

        return ModerationResponse.success(
                ModerationAction.APPROVE_REGISTRATION,
                ModerationTargetType.EVENT,
                roleInEventId.toString(),
                ModerationStatus.APPROVED,
                "Registration approved"
        );
    }

    // ========================= REJECT =========================

    @Override
    public ModerationResponse rejectRegistration(Long roleInEventId) {

        RoleInEvent roleInEvent = roleInEventRepository.findById(roleInEventId).orElse(null);
        if (roleInEvent == null) {
            return failure(
                    ModerationAction.REJECT_REGISTRATION,
                    roleInEventId,
                    "ROLE_NOT_FOUND"
            );
        }

        if (roleInEvent.getParticipationStatus() != ParticipationStatus.PENDING) {
            return failure(
                    ModerationAction.REJECT_REGISTRATION,
                    roleInEventId,
                    "ROLE_ALREADY_PROCESSED"
            );
        }

        roleInEvent.setParticipationStatus(ParticipationStatus.REJECTED);
        roleInEventRepository.save(roleInEvent);

        // ===== sync EventRegistration =====
        eventRegistrationRepository
                .findByUserIdAndEventIdAndStatus(
                        roleInEvent.getUserProfile().getUserId(),
                        roleInEvent.getEvent().getEventId(),
                        RegistrationStatus.PENDING
                )
                .ifPresent(reg -> {
                    reg.setStatus(RegistrationStatus.REJECTED);
                    eventRegistrationRepository.save(reg);
                });

        return ModerationResponse.success(
                ModerationAction.REJECT_REGISTRATION,
                ModerationTargetType.EVENT,
                roleInEventId.toString(),
                ModerationStatus.REJECTED,
                "Registration rejected"
        );
    }

    // ========================= UNREGISTER =========================

    @Override
    public ModerationResponse unregisterEvent(UUID userId, Long eventId) {

        RoleInEvent roleInEvent = roleInEventRepository
                .findByUserProfile_UserIdAndEvent_EventId(userId, eventId)
                .orElse(null);

        if (roleInEvent == null || roleInEvent.getParticipationStatus() != ParticipationStatus.PENDING) {
            return failure(
                    ModerationAction.UNREGISTER_EVENT,
                    eventId,
                    "ROLE_NOT_PENDING"
            );
        }

        roleInEvent.setParticipationStatus(ParticipationStatus.CANCELLED);
        roleInEventRepository.save(roleInEvent);

        // ===== sync EventRegistration =====
        eventRegistrationRepository
                .findByUserIdAndEventIdAndStatus(
                        userId, eventId, RegistrationStatus.PENDING
                )
                .ifPresent(reg -> {
                    reg.setStatus(RegistrationStatus.CANCELLED_BY_USER);
                    eventRegistrationRepository.save(reg);
                });

        return ModerationResponse.success(
                ModerationAction.UNREGISTER_EVENT,
                ModerationTargetType.EVENT,
                roleInEvent.getId().toString(),
                ModerationStatus.UNREGISTERED,
                "Registration cancelled"
        );
    }

    // ========================= UTIL =========================

    private ModerationResponse failure(
            ModerationAction action,
            Object targetId,
            String reasonCode
    ) {
        return ModerationResponse.failure(
                action,
                ModerationTargetType.EVENT,
                targetId.toString(),
                ModerationResult.INVALID,
                ModerationStatus.DENIED,
                reasonCode,
                reasonCode
        );
    }
}
