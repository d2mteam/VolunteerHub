package com.volunteerhub.community.service.write_service.impl;

import com.volunteerhub.community.dto.ModerationAction;
import com.volunteerhub.community.dto.ModerationResponse;
import com.volunteerhub.community.dto.ModerationResult;
import com.volunteerhub.community.dto.ModerationStatus;
import com.volunteerhub.community.dto.ModerationTargetType;
import com.volunteerhub.community.model.Event;
import com.volunteerhub.community.model.EventRegistration;
import com.volunteerhub.community.model.RoleInEvent;
import com.volunteerhub.community.model.UserProfile;
import com.volunteerhub.community.model.db_enum.ParticipationStatus;
import com.volunteerhub.community.model.db_enum.RegistrationStatus;
import com.volunteerhub.community.repository.EventRegistrationRepository;
import com.volunteerhub.community.repository.EventRepository;
import com.volunteerhub.community.repository.RoleInEventRepository;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.community.service.write_service.IEventRegistrationService;
import com.volunteerhub.ultis.SnowflakeIdGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class EventRegistrationService implements IEventRegistrationService {

    private static final Set<ParticipationStatus> ACTIVE_PARTICIPATION =
            Set.of(ParticipationStatus.APPROVED, ParticipationStatus.COMPLETED);

    private final EventRegistrationRepository eventRegistrationRepository;
    private final RoleInEventRepository roleInEventRepository;
    private final EventRepository eventRepository;
    private final UserProfileRepository userProfileRepository;
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    public ModerationResponse approveRegistration(Long registrationId) {
        EventRegistration reg = eventRegistrationRepository.findById(registrationId).orElse(null);

        if (reg == null) {
            return ModerationResponse.failure(
                    ModerationAction.APPROVE_REGISTRATION,
                    ModerationTargetType.EVENT_REGISTRATION,
                    registrationId.toString(),
                    ModerationResult.NOT_FOUND,
                    ModerationStatus.FAILED,
                    String.format("Registration not found (registrationId: %d)", registrationId),
                    "REGISTRATION_NOT_FOUND"
            );
        }

        if (reg.getStatus() != RegistrationStatus.PENDING) {
            return ModerationResponse.failure(
                    ModerationAction.APPROVE_REGISTRATION,
                    ModerationTargetType.EVENT_REGISTRATION,
                    registrationId.toString(),
                    ModerationResult.INVALID,
                    ModerationStatus.DENIED,
                    "Registration cannot be approved because it has already been processed",
                    "REGISTRATION_ALREADY_PROCESSED"
            );
        }

        Long eventId = reg.getEventId();
        UUID userId = reg.getUserId();

        Optional<RoleInEvent> existingRole = roleInEventRepository
                .findByUserProfile_UserIdAndEvent_EventId(userId, eventId);
        if (existingRole.map(RoleInEvent::getParticipationStatus).filter(ACTIVE_PARTICIPATION::contains).isPresent()) {
            return ModerationResponse.failure(
                    ModerationAction.APPROVE_REGISTRATION,
                    ModerationTargetType.EVENT_REGISTRATION,
                    registrationId.toString(),
                    ModerationResult.INVALID,
                    ModerationStatus.DENIED,
                    String.format("User already registered for this event (eventId: %d)", eventId),
                    "USER_ALREADY_REGISTERED"
            );
        }

        reg.setStatus(RegistrationStatus.APPROVED);
        eventRegistrationRepository.save(reg);

        RoleInEvent roleInEvent = existingRole.orElseGet(() -> RoleInEvent.builder()
                .id(snowflakeIdGenerator.nextId())
                .event(reg.getEvent())
                .userProfile(reg.getUserProfile())
                .build());
        roleInEvent.setParticipationStatus(ParticipationStatus.APPROVED);
        roleInEventRepository.save(roleInEvent);

        return ModerationResponse.success(
                ModerationAction.APPROVE_REGISTRATION,
                ModerationTargetType.EVENT_REGISTRATION,
                registrationId.toString(),
                ModerationStatus.APPROVED,
                "Registration approved"
        );
    }

    @Override
    public ModerationResponse rejectRegistration(Long registrationId) {
        EventRegistration reg = eventRegistrationRepository.findById(registrationId).orElse(null);

        if (reg == null) {
            return ModerationResponse.failure(
                    ModerationAction.REJECT_REGISTRATION,
                    ModerationTargetType.EVENT_REGISTRATION,
                    registrationId.toString(),
                    ModerationResult.NOT_FOUND,
                    ModerationStatus.FAILED,
                    String.format("Registration not found (registrationId: %d)", registrationId),
                    "REGISTRATION_NOT_FOUND"
            );
        }

        if (reg.getStatus() != RegistrationStatus.PENDING) {
            return ModerationResponse.failure(
                    ModerationAction.REJECT_REGISTRATION,
                    ModerationTargetType.EVENT_REGISTRATION,
                    registrationId.toString(),
                    ModerationResult.INVALID,
                    ModerationStatus.DENIED,
                    "Registration cannot be updated because it has already been processed",
                    "REGISTRATION_ALREADY_PROCESSED"
            );
        }

        Long eventId = reg.getEventId();
        UUID userId = reg.getUserId();

        if (roleInEventRepository.findByUserProfile_UserIdAndEvent_EventId(userId, eventId)
                .map(RoleInEvent::getParticipationStatus)
                .filter(ACTIVE_PARTICIPATION::contains)
                .isPresent()) {
            return ModerationResponse.failure(
                    ModerationAction.REJECT_REGISTRATION,
                    ModerationTargetType.EVENT_REGISTRATION,
                    registrationId.toString(),
                    ModerationResult.INVALID,
                    ModerationStatus.DENIED,
                    String.format("User already registered for this event (eventId: %d)", eventId),
                    "USER_ALREADY_REGISTERED"
            );
        }

        reg.setStatus(RegistrationStatus.REJECTED);
        eventRegistrationRepository.save(reg);

        return ModerationResponse.success(
                ModerationAction.REJECT_REGISTRATION,
                ModerationTargetType.EVENT_REGISTRATION,
                registrationId.toString(),
                ModerationStatus.REJECTED,
                "Registration rejected"
        );
    }

    @Override
    public ModerationResponse registerEvent(UUID userId, Long eventId) {

        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isEmpty()) {
            return ModerationResponse.failure(
                    ModerationAction.REGISTER_EVENT,
                    ModerationTargetType.EVENT_REGISTRATION,
                    eventId.toString(),
                    ModerationResult.NOT_FOUND,
                    ModerationStatus.FAILED,
                    String.format("Event not found (eventId: %d)", eventId),
                    "EVENT_NOT_FOUND"
            );
        }

        Optional<EventRegistration> pendingRegistration = eventRegistrationRepository
                .findByUserIdAndEventIdAndStatus(userId, eventId, RegistrationStatus.PENDING);
        if (pendingRegistration.isPresent()) {
            return ModerationResponse.failure(
                    ModerationAction.REGISTER_EVENT,
                    ModerationTargetType.EVENT_REGISTRATION,
                    pendingRegistration.get().getRegistrationId().toString(),
                    ModerationResult.INVALID,
                    ModerationStatus.DENIED,
                    "Registration is already pending",
                    "REGISTRATION_PENDING"
            );
        }

        if (roleInEventRepository.findByUserProfile_UserIdAndEvent_EventId(userId, eventId)
                .map(RoleInEvent::getParticipationStatus)
                .filter(ACTIVE_PARTICIPATION::contains)
                .isPresent()) {
            return ModerationResponse.failure(
                    ModerationAction.REGISTER_EVENT,
                    ModerationTargetType.EVENT_REGISTRATION,
                    eventId.toString(),
                    ModerationResult.INVALID,
                    ModerationStatus.DENIED,
                    String.format("User already registered for this event (eventId: %d)", eventId),
                    "USER_ALREADY_REGISTERED"
            );
        }

        UserProfile userProfile = userProfileRepository.getReferenceById(userId);
        Event event = eventOptional.get();

        EventRegistration reg = EventRegistration.builder()
                .registrationId(snowflakeIdGenerator.nextId())
                .userProfile(userProfile)
                .event(event)
                .build();

        eventRegistrationRepository.save(reg);

        return ModerationResponse.success(
                ModerationAction.REGISTER_EVENT,
                ModerationTargetType.EVENT_REGISTRATION,
                reg.getRegistrationId().toString(),
                ModerationStatus.REGISTERED,
                "Registration created"
        );
    }

    @Override
    public ModerationResponse unregisterEvent(UUID userId, Long eventId) {
        EventRegistration reg = eventRegistrationRepository.findByUserIdAndEventIdAndStatus(
                userId, eventId, RegistrationStatus.PENDING).orElse(null);

        if (reg == null) {
            return ModerationResponse.failure(
                    ModerationAction.UNREGISTER_EVENT,
                    ModerationTargetType.EVENT_REGISTRATION,
                    eventId.toString(),
                    ModerationResult.NOT_FOUND,
                    ModerationStatus.FAILED,
                    "Unable to unregister because this registration either does not exist or has already been processed",
                    "REGISTRATION_NOT_FOUND_OR_PROCESSED"
            );
        }

        reg.setStatus(RegistrationStatus.CANCELLED_BY_USER);
        eventRegistrationRepository.save(reg);

        return ModerationResponse.success(
                ModerationAction.UNREGISTER_EVENT,
                ModerationTargetType.EVENT_REGISTRATION,
                reg.getRegistrationId().toString(),
                ModerationStatus.UNREGISTERED,
                "Registration cancelled"
        );
    }
}
