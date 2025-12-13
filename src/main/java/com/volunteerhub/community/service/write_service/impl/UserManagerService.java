package com.volunteerhub.community.service.write_service.impl;

import com.volunteerhub.community.dto.ModerationAction;
import com.volunteerhub.community.dto.ModerationResponse;
import com.volunteerhub.community.dto.ModerationStatus;
import com.volunteerhub.community.model.db_enum.UserStatus;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.community.service.write_service.IUserManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserManagerService implements IUserManagerService {
    private final UserProfileRepository userProfileRepository;

    @Override
    public ModerationResponse banUser(UUID userId) {
        int updated = userProfileRepository.updateStatus(userId, UserStatus.BANNED);

        if (updated == 0) {
            return ModerationResponse.failure(
                    ModerationAction.BAN_USER,
                    "USER",
                    userId.toString(),
                    String.format("User with ID %s does not exist", userId));
        }

        return ModerationResponse.success(
                ModerationAction.BAN_USER,
                "USER",
                userId.toString(),
                ModerationStatus.BANNED,
                String.format("User %s has been banned", userId),
                LocalDateTime.now()
        );
    }

    @Override
    public ModerationResponse unbanUser(UUID userId) {
        int updated = userProfileRepository.updateStatus(userId, UserStatus.ACTIVE);

        if (updated == 0) {
            return ModerationResponse.failure(
                    ModerationAction.UNBAN_USER,
                    "USER",
                    userId.toString(),
                    String.format("User with ID %s does not exist", userId));
        }

        return ModerationResponse.success(
                ModerationAction.UNBAN_USER,
                "USER",
                userId.toString(),
                ModerationStatus.UNBANNED,
                String.format("User %s has been unbanned", userId),
                LocalDateTime.now()
        );
    }

}
