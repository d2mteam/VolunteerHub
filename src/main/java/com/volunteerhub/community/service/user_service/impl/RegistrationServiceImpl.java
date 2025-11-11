package com.volunteerhub.community.service.user_service.impl;

import com.volunteerhub.community.dto.graphql.input.RegistrationInput;
import com.volunteerhub.community.dto.graphql.output.ActionResponse;
import com.volunteerhub.community.dto.graphql.output.RegistrationDto;
import com.volunteerhub.community.dto.graphql.page.OffsetPage;
import com.volunteerhub.community.dto.graphql.page.PageInfo;
import com.volunteerhub.community.dto.graphql.page.PageUtils;
import com.volunteerhub.community.entity.Event;
import com.volunteerhub.community.entity.Registration;
import com.volunteerhub.community.entity.UserProfile;
import com.volunteerhub.community.entity.db_enum.ApprovalStatus;
import com.volunteerhub.community.repository.EventRepository;
import com.volunteerhub.community.repository.RegistrationRepository;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.community.service.user_service.IRegistrationService;


import com.volunteerhub.ultis.SnowflakeIdGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RegistrationServiceImpl implements IRegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserProfileRepository userProfileRepository;
    private final SnowflakeIdGenerator idGenerator;

    @Override
    public ActionResponse<Void> registerToEvent(UUID userId, RegistrationInput input) {
        UserProfile user = userProfileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Event event = eventRepository.findById(input.getEventId())
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        boolean alreadyExists = registrationRepository.existsByEvent_EventIdAndCreatedBy_UserId(
                input.getEventId(), userId
        );
        if (alreadyExists) {
            return ActionResponse.failure("User already registered for this event");
        }

        Registration registration = Registration.builder()
                .registrationId(idGenerator.nextId())
                .event(event)
                .createdBy(user)
                .approvalStatus(ApprovalStatus.PENDING)
                .extraInfo(input.getExtraInfos())
                .build();

        registrationRepository.save(registration);

        LocalDateTime now = LocalDateTime.now();
        return ActionResponse.success(
                registration.getRegistrationId().toString(),
                now,
                now
        );
    }

    @Override
    public ActionResponse<Void> cancelRegistration(UUID userId, int registrationId) {
        Registration registration = registrationRepository.findById((long) registrationId)
                .orElseThrow(() -> new EntityNotFoundException("Registration not found"));

        if (!registration.getCreatedBy().getUserId().equals(userId)) {
            return ActionResponse.failure("Permission denied: cannot cancel another user's registration");
        }

        registration.setApprovalStatus(ApprovalStatus.CANCELLED);
        registrationRepository.save(registration);

        LocalDateTime now = LocalDateTime.now();
        return ActionResponse.success(
                registration.getRegistrationId().toString(),
                now,
                now
        );
    }

    @Override
    public ActionResponse<Void> updateRegistration(UUID userId, int registrationId, Map<String, Object> extraInfos) {
        Registration registration = registrationRepository.findById((long) registrationId)
                .orElseThrow(() -> new EntityNotFoundException("Registration not found"));

        if (!registration.getCreatedBy().getUserId().equals(userId)) {
            return ActionResponse.failure("Permission denied: cannot update another user's registration");
        }

        registration.setExtraInfo(extraInfos);
        registrationRepository.save(registration);

        LocalDateTime now = LocalDateTime.now();
        return ActionResponse.success(
                registration.getRegistrationId().toString(),
                now,
                now
        );
    }

    @Override
    public ActionResponse<OffsetPage<RegistrationDto>> getRegistrationsByUserId(UUID userId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Registration> registrationPage = registrationRepository.findByCreatedBy_UserId(userId, pageable);

        List<RegistrationDto> content = registrationPage.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        PageInfo pageInfo = PageUtils.from(registrationPage);
        OffsetPage<RegistrationDto> offsetPage = OffsetPage.<RegistrationDto>builder()
                .content(content)
                .pageInfo(pageInfo)
                .build();

        LocalDateTime now = LocalDateTime.now();
        return ActionResponse.success(
                "user_" + userId,
                now,
                now,
                offsetPage
        );
    }

    private RegistrationDto toDto(Registration reg) {
        UserProfile u = reg.getCreatedBy();
        return new RegistrationDto(
                reg.getRegistrationId(),
                u != null ? u.getUserId() : null,
                u != null ? u.getUsername() : null,
                u != null ? u.getFullName() : null,
                reg.getApprovalStatus() != null ? reg.getApprovalStatus().toString() : null,
                reg.getExtraInfo()
        );
    }
}
