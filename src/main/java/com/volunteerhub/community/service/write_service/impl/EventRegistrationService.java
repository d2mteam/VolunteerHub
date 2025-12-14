package com.volunteerhub.community.service.write_service.impl;

import com.volunteerhub.community.dto.ModerationAction;
import com.volunteerhub.community.dto.ModerationResponse;
import com.volunteerhub.community.dto.ModerationStatus;
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

import java.time.LocalDateTime;
import java.util.List;
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
                    "EVENT_REGISTRATION",
                    registrationId.toString(),
                    String.format("Registration not found (registrationId: %d)", registrationId));
        }

        if (reg.getStatus() != RegistrationStatus.PENDING) {
            return ModerationResponse.failure(
                    ModerationAction.APPROVE_REGISTRATION,
                    "EVENT_REGISTRATION",
                    registrationId.toString(),
                    "Registration cannot be approved because it has already been processed");
        }

        Long eventId = reg.getEventId();
        UUID userId = reg.getUserId();

        Optional<RoleInEvent> existingRole = roleInEventRepository
                .findByUserProfile_UserIdAndEvent_EventId(userId, eventId);
        if (existingRole.map(RoleInEvent::getParticipationStatus).filter(ACTIVE_PARTICIPATION::contains).isPresent()) {
            return ModerationResponse.failure(
                    ModerationAction.APPROVE_REGISTRATION,
                    "EVENT_REGISTRATION",
                    registrationId.toString(),
                    String.format("User already registered for this event (eventId: %d)", eventId));
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
                "EVENT_REGISTRATION",
                registrationId.toString(),
                ModerationStatus.APPROVED,
                "Registration approved",
                LocalDateTime.now());
    }

    @Override
    public ModerationResponse rejectRegistration(Long registrationId) {
        EventRegistration reg = eventRegistrationRepository.findById(registrationId).orElse(null);

        if (reg == null) {
            return ModerationResponse.failure(
                    ModerationAction.REJECT_REGISTRATION,
                    "EVENT_REGISTRATION",
                    registrationId.toString(),
                    String.format("Registration not found (registrationId: %d)", registrationId));
        }

        if (reg.getStatus() != RegistrationStatus.PENDING) {
            return ModerationResponse.failure(
                    ModerationAction.REJECT_REGISTRATION,
                    "EVENT_REGISTRATION",
                    registrationId.toString(),
                    "Registration cannot be updated because it has already been processed");
        }

        Long eventId = reg.getEventId();
        UUID userId = reg.getUserId();

        if (roleInEventRepository.findByUserProfile_UserIdAndEvent_EventId(userId, eventId)
                .map(RoleInEvent::getParticipationStatus)
                .filter(ACTIVE_PARTICIPATION::contains)
                .isPresent()) {
            return ModerationResponse.failure(
                    ModerationAction.REJECT_REGISTRATION,
                    "EVENT_REGISTRATION",
                    registrationId.toString(),
                    String.format("User already registered for this event (eventId: %d)", eventId));
        }

        reg.setStatus(RegistrationStatus.REJECTED);
        eventRegistrationRepository.save(reg);

        return ModerationResponse.success(
                ModerationAction.REJECT_REGISTRATION,
                "EVENT_REGISTRATION",
                registrationId.toString(),
                ModerationStatus.REJECTED,
                "Registration rejected",
                LocalDateTime.now());
    }

    @Override
    public ModerationResponse registerEvent(UUID userId, Long eventId) {

        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isEmpty()) {
            return ModerationResponse.failure(
                    ModerationAction.REGISTER_EVENT,
                    "EVENT_REGISTRATION",
                    eventId.toString(),
                    String.format("Event not found (eventId: %d)", eventId));
        }

        Optional<EventRegistration> pendingRegistration = eventRegistrationRepository
                .findByUserIdAndEventIdAndStatus(userId, eventId, RegistrationStatus.PENDING);
        if (pendingRegistration.isPresent()) {
            return ModerationResponse.failure(
                    ModerationAction.REGISTER_EVENT,
                    "EVENT_REGISTRATION",
                    pendingRegistration.get().getRegistrationId().toString(),
                    "Registration is already pending");
        }

        if (roleInEventRepository.findByUserProfile_UserIdAndEvent_EventId(userId, eventId)
                .map(RoleInEvent::getParticipationStatus)
                .filter(ACTIVE_PARTICIPATION::contains)
                .isPresent()) {
            return ModerationResponse.failure(
                    ModerationAction.REGISTER_EVENT,
                    "EVENT_REGISTRATION",
                    eventId.toString(),
                    String.format("User already registered for this event (eventId: %d)", eventId));
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
                "EVENT_REGISTRATION",
                reg.getRegistrationId().toString(),
                ModerationStatus.REGISTERED,
                "Registration created",
                LocalDateTime.now());
    }

    @Override
    public ModerationResponse unregisterEvent(UUID userId, Long eventId) {
        EventRegistration reg = eventRegistrationRepository.findByUserIdAndEventIdAndStatus(
                userId, eventId, RegistrationStatus.PENDING).orElse(null);

        if (reg == null) {
            return ModerationResponse.failure(
                    ModerationAction.UNREGISTER_EVENT,
                    "EVENT_REGISTRATION",
                    eventId.toString(),
                    "Unable to unregister because this registration either does not exist or has already been processed");
        }

        reg.setStatus(RegistrationStatus.CANCELLED_BY_USER);
        eventRegistrationRepository.save(reg);

        return ModerationResponse.success(
                ModerationAction.UNREGISTER_EVENT,
                "EVENT_REGISTRATION",
                reg.getRegistrationId().toString(),
                ModerationStatus.UNREGISTERED,
                "Registration cancelled",
                LocalDateTime.now());
    }
}
