package com.volunteerhub.community.service.management_service.impl;

import com.volunteerhub.community.dto.graphql.output.ActionResponse;
import com.volunteerhub.community.dto.graphql.output.RegistrationDto;
import com.volunteerhub.community.dto.graphql.page.OffsetPage;
import com.volunteerhub.community.dto.graphql.page.PageInfo;
import com.volunteerhub.community.dto.graphql.page.PageUtils;
import com.volunteerhub.community.entity.Registration;
import com.volunteerhub.community.entity.db_enum.ApprovalStatus;
import com.volunteerhub.community.repository.RegistrationRepository;
import com.volunteerhub.community.service.management_service.IRegistrationManagerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RegistrationManagerServiceImpl implements IRegistrationManagerService {

    private final RegistrationRepository registrationRepository;

    @Override
    public ActionResponse<Void> approveRegistration(Long registrationId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new EntityNotFoundException("Registration not found"));

        registration.setApprovalStatus(ApprovalStatus.APPROVED);

        registrationRepository.save(registration);

        LocalDateTime now = LocalDateTime.now();
        return ActionResponse.success(
                registration.getRegistrationId().toString(),
                now,
                now
        );
    }

    @Override
    public ActionResponse<Void> rejectRegistration(Long registrationId, String reason) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new EntityNotFoundException("Registration not found"));

        registration.setApprovalStatus(ApprovalStatus.REJECTED);

        registrationRepository.save(registration);

        LocalDateTime now = LocalDateTime.now();
        return ActionResponse.success(
                registration.getRegistrationId().toString(),
                now,
                now
        );
    }

    @Override
    public ActionResponse<OffsetPage<RegistrationDto>> findRegistrationsInEvent(long eventId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Registration> registrationPage = registrationRepository.findByEvent_EventId(eventId, pageable);

        List<RegistrationDto> content = registrationPage.stream()
                .map(reg -> new RegistrationDto(
                        reg.getRegistrationId(),
                        reg.getCreatedBy() != null ? reg.getCreatedBy().getUserId() : null,
                        reg.getCreatedBy() != null ? reg.getCreatedBy().getUsername() : null,
                        reg.getCreatedBy() != null ? reg.getCreatedBy().getFullName() : null,
                        reg.getApprovalStatus().toString() != null ? reg.getApprovalStatus().toString() : null,
                        reg.getExtraInfo()
                ))
                .collect(Collectors.toList());

        PageInfo pageInfo = PageUtils.from(registrationPage);
        OffsetPage<RegistrationDto> offsetPage = OffsetPage.<RegistrationDto>builder()
                .content(content)
                .pageInfo(pageInfo)
                .build();

        LocalDateTime now = LocalDateTime.now();
        return ActionResponse.success(
                "event_" + eventId,
                now,
                now,
                offsetPage
        );
    }
}
