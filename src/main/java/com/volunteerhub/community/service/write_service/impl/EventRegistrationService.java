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
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class EventRegistrationService implements IEventRegistrationService {

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

        if (roleInEventRepository.existsByUserProfile_UserIdAndEvent_EventIdAndParticipationStatusIn(
                userId, eventId, List.of(ParticipationStatus.APPROVED, ParticipationStatus.COMPLETED))) {
            return ActionResponse.failure(
                    String.format("User already registered for this event (eventId: %d)", eventId));
        }

        reg.setStatus(RegistrationStatus.APPROVED);
        eventRegistrationRepository.save(reg);

        RoleInEvent roleInEvent = RoleInEvent.builder()
                .event(reg.getEvent())
                .userProfile(reg.getUserProfile())
                .build();
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

        if (roleInEventRepository.existsByUserProfile_UserIdAndEvent_EventIdAndParticipationStatusIn(
                userId, eventId, List.of(ParticipationStatus.APPROVED, ParticipationStatus.COMPLETED))) {
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

        if (eventRegistrationRepository.existsByUserIdAndEventIdAndStatus(
                userId, eventId, RegistrationStatus.PENDING)) {
            return ActionResponse.failure("Registration is already pending");
        }

        if (!eventRepository.existsById(eventId)) {
            return ActionResponse.failure(
                    String.format("Event not found (eventId: %d)", eventId));
        }

        if (roleInEventRepository.existsByUserProfile_UserIdAndEvent_EventIdAndParticipationStatusIn(
                userId, eventId, List.of(ParticipationStatus.APPROVED, ParticipationStatus.COMPLETED))) {
            return ActionResponse.failure(
                    String.format("User already registered for this event (eventId: %d)", eventId));
        }

        UserProfile userProfile = userProfileRepository.getReferenceById(userId);
        Event event = eventRepository.getReferenceById(eventId);

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
