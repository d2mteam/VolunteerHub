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
    private final EventRegistrationRepository eventRegistrationRepo;
    private final RoleInEventRepository roleInEventRepo;
    private final EventRepository eventRepo;
    private final UserProfileRepository userProfileRepo;
    private final SnowflakeIdGenerator snowflakeIdGenerator;


    @Override
    public ActionResponse<Void> approveRegistration(Long registrationId) {
        EventRegistration reg = eventRegistrationRepo.findById(registrationId).orElse(null);

        if (reg == null) {
            return ActionResponse.failure("Registration not found, registrationId: " + registrationId);
        }

        if (reg.getStatus() != RegistrationStatus.PENDING) {
            return ActionResponse.failure("Cannot register this registration " +
                    "because it is already approved, rejected or cancelled, registrationId: " + registrationId);
        }

        Long eventId = reg.getEventId();
        UUID userId = reg.getUserId();

        if (roleInEventRepo.existsByUserProfile_UserIdAndEvent_EventIdAndParticipationStatusIn(
                userId, eventId, List.of(ParticipationStatus.APPROVED, ParticipationStatus.COMPLETED))) {
            return ActionResponse.failure("User already registered this event, eventId: " + eventId);
        }

        reg.setStatus(RegistrationStatus.APPROVED);
        eventRegistrationRepo.save(reg);
        RoleInEvent roleInEvent = RoleInEvent.builder()
                .event(reg.getEvent())
                .userProfile(reg.getUserProfile())
                .build();
        roleInEventRepo.save(roleInEvent);
        return ActionResponse.success(
                registrationId.toString(),
                null,
                LocalDateTime.now());
    }

    @Override
    public ActionResponse<Void> rejectRegistration(Long registrationId) {
        EventRegistration reg = eventRegistrationRepo.findById(registrationId).orElse(null);

        if (reg == null) {
            return ActionResponse.failure("Registration not found, registrationId: " + registrationId);
        }

        if (reg.getStatus() != RegistrationStatus.PENDING) {
            return ActionResponse.failure("Cannot unregister this registration " +
                    "because it is already approved, rejected or cancelled, registrationId: " + registrationId);
        }

        Long eventId = reg.getEventId();
        UUID userId = reg.getUserId();

        if (roleInEventRepo.existsByUserProfile_UserIdAndEvent_EventIdAndParticipationStatusIn(
                userId, eventId, List.of(ParticipationStatus.APPROVED, ParticipationStatus.COMPLETED))) {
            return ActionResponse.failure("User already registered this event, eventId: " + eventId);
        }

        reg.setStatus(RegistrationStatus.REJECTED);
        eventRegistrationRepo.save(reg);
        return ActionResponse.success(
                registrationId.toString(),
                null,
                LocalDateTime.now());
    }

    @Override
    public ActionResponse<Void> registerEvent(UUID userId, Long eventId) {
        if (eventRegistrationRepo.existsByUserIdAndEventIdAndStatus(
                userId, eventId, RegistrationStatus.PENDING)) {
            return ActionResponse.failure("Event registration has already been pending.");
        }

        if (!eventRepo.existsById(eventId)) {
            return ActionResponse.failure("Event not found, eventId: " + eventId);
        }

        if (roleInEventRepo.existsByUserProfile_UserIdAndEvent_EventIdAndParticipationStatusIn(
                userId, eventId, List.of(ParticipationStatus.APPROVED, ParticipationStatus.COMPLETED))) {
            return ActionResponse.failure("User already registered this event, eventId: " + eventId);
        }

        UserProfile userProfile = userProfileRepo.getReferenceById(userId);
        Event event = eventRepo.getReferenceById(eventId);
        EventRegistration reg = EventRegistration.builder()
                .registrationId(snowflakeIdGenerator.nextId())
                .userProfile(userProfile)
                .event(event)
                .build();
        eventRegistrationRepo.save(reg);
        return ActionResponse.success(
                reg.getRegistrationId().toString(),
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    @Override
    public ActionResponse<Void> unregisterEvent(UUID userId, Long eventId) {
        EventRegistration reg = eventRegistrationRepo.findByUserIdAndEventIdAndStatus(
                userId, eventId, RegistrationStatus.PENDING).orElse(null);

        if (reg == null) {
            return ActionResponse.failure("Cannot unregister. " +
                    "This registration is either not found or already processed.");
        }
        reg.setStatus(RegistrationStatus.CANCELLED_BY_USER);
        eventRegistrationRepo.save(reg);
        return ActionResponse.success(
                reg.getRegistrationId().toString(),
                null,
                LocalDateTime.now());
    }
}
