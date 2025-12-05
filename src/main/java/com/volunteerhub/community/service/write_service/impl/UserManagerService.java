package com.volunteerhub.community.service.write_service.impl;

import com.volunteerhub.community.dto.ActionResponse;
import com.volunteerhub.community.model.db_enum.UserStatus;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.community.service.write_service.IUserManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserManagerService implements IUserManagerService {
    private final UserProfileRepository userProfileRepository;

    @Override
    public ActionResponse<Void> banUser(UUID userId) {
        int updated = userProfileRepository.updateStatus(userId, UserStatus.BANNED);

        if (updated == 0) {
            return ActionResponse.failure(String.format("User with ID %s does not exist", userId));
        }

        return ActionResponse.success(
                String.format("User %s has been banned", userId),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Override
    public ActionResponse<Void> unbanUser(UUID userId) {
        int updated = userProfileRepository.updateStatus(userId, UserStatus.ACTIVE);

        if (updated == 0) {
            return ActionResponse.failure(String.format("User with ID %s does not exist", userId));
        }

        return ActionResponse.success(
                String.format("User %s has been unbanned", userId),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

}
