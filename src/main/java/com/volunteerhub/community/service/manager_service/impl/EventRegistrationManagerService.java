package com.volunteerhub.community.service.manager_service.impl;

import com.volunteerhub.community.dto.graphql.output.ActionResponse;
import com.volunteerhub.community.model.table.EventRegistration;
import com.volunteerhub.community.model.table.RoleInEvent;
import com.volunteerhub.community.model.db_enum.ParticipationStatus;
import com.volunteerhub.community.model.db_enum.RegistrationStatus;
import com.volunteerhub.community.repository.EventRegistrationRepository;
import com.volunteerhub.community.repository.RoleInEventRepository;
import com.volunteerhub.community.service.manager_service.IEventRegistrationManagerService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class EventRegistrationManagerService implements IEventRegistrationManagerService {

    private final EventRegistrationRepository eventRegistrationRepo;
    private final RoleInEventRepository roleInEventRepo;

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
}
