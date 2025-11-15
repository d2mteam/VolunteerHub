package com.volunteerhub.community.service.manager_service;

import com.volunteerhub.community.dto.graphql.output.ActionResponse;
import com.volunteerhub.community.entity.EventRegistration;
import com.volunteerhub.community.entity.RoleInEvent;
import com.volunteerhub.community.entity.db_enum.RegistrationStatus;
import com.volunteerhub.community.repository.EventRegistrationRepository;
import com.volunteerhub.community.repository.RoleInEventRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
            return ActionResponse.failure("Registration not found" + registrationId);
        }

        if (reg.getStatus() != RegistrationStatus.PENDING) {
            return ActionResponse.failure("Cannot unregister this registration because it has already been approved or rejected "
                    + registrationId);
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
            return ActionResponse.failure("Registration not found" + registrationId);
        }

        if (reg.getStatus() != RegistrationStatus.PENDING) {
            return ActionResponse.failure("Cannot unregister this registration because it has already been approved or rejected "
                    + registrationId);
        }

        reg.setStatus(RegistrationStatus.REJECTED);
        eventRegistrationRepo.save(reg);
        return ActionResponse.success(
                registrationId.toString(),
                null,
                LocalDateTime.now());
    }
}
