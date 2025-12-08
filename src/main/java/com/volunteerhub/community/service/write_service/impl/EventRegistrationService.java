package com.volunteerhub.community.service.write_service.impl;

import com.volunteerhub.community.dto.ActionResponse;
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
    public ActionResponse<Void> approveRegistration(Long registrationId) {
        EventRegistration reg = eventRegistrationRepository.findById(registrationId).orElse(null);

        if (reg == null) {
            return ActionResponse.failure(
                    String.format("Registration not found (registrationId: %d)", registrationId));
        }

        if (reg.getStatus() != RegistrationStatus.PENDING) {
            return ActionResponse.failure(
                    "Registration cannot be approved because it has already been processed");
        }

        Long eventId = reg.getEventId();
        UUID userId = reg.getUserId();

        Optional<RoleInEvent> existingRole = roleInEventRepository
                .findByUserProfile_UserIdAndEvent_EventId(userId, eventId);
        if (existingRole.map(RoleInEvent::getParticipationStatus).filter(ACTIVE_PARTICIPATION::contains).isPresent()) {
            return ActionResponse.failure(
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

        return ActionResponse.success(
                registrationId.toString(),
                null,
                LocalDateTime.now());
    }

    @Override
    public ActionResponse<Void> rejectRegistration(Long registrationId) {
        EventRegistration reg = eventRegistrationRepository.findById(registrationId).orElse(null);

        if (reg == null) {
            return ActionResponse.failure(
                    String.format("Registration not found (registrationId: %d)", registrationId));
        }

        if (reg.getStatus() != RegistrationStatus.PENDING) {
            return ActionResponse.failure(
                    "Registration cannot be updated because it has already been processed");
        }

        Long eventId = reg.getEventId();
        UUID userId = reg.getUserId();

        if (roleInEventRepository.findByUserProfile_UserIdAndEvent_EventId(userId, eventId)
                .map(RoleInEvent::getParticipationStatus)
                .filter(ACTIVE_PARTICIPATION::contains)
                .isPresent()) {
            return ActionResponse.failure(
                    String.format("User already registered for this event (eventId: %d)", eventId));
        }

        reg.setStatus(RegistrationStatus.REJECTED);
        eventRegistrationRepository.save(reg);

        return ActionResponse.success(
                registrationId.toString(),
                null,
                LocalDateTime.now());
    }

    @Override
    public ActionResponse<Void> registerEvent(UUID userId, Long eventId) {

        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isEmpty()) {
            return ActionResponse.failure(
                    String.format("Event not found (eventId: %d)", eventId));
        }

        Optional<EventRegistration> pendingRegistration = eventRegistrationRepository
                .findByUserIdAndEventIdAndStatus(userId, eventId, RegistrationStatus.PENDING);
        if (pendingRegistration.isPresent()) {
            return ActionResponse.failure("Registration is already pending");
        }

        if (roleInEventRepository.findByUserProfile_UserIdAndEvent_EventId(userId, eventId)
                .map(RoleInEvent::getParticipationStatus)
                .filter(ACTIVE_PARTICIPATION::contains)
                .isPresent()) {
            return ActionResponse.failure(
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

        return ActionResponse.success(
                reg.getRegistrationId().toString(),
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    @Override
    public ActionResponse<Void> unregisterEvent(UUID userId, Long eventId) {
        EventRegistration reg = eventRegistrationRepository.findByUserIdAndEventIdAndStatus(
                userId, eventId, RegistrationStatus.PENDING).orElse(null);

        if (reg == null) {
            return ActionResponse.failure(
                    "Unable to unregister because this registration either does not exist or has already been processed");
        }

        reg.setStatus(RegistrationStatus.CANCELLED_BY_USER);
        eventRegistrationRepository.save(reg);

        return ActionResponse.success(
                reg.getRegistrationId().toString(),
                null,
                LocalDateTime.now());
    }
}
