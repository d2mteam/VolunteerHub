package com.volunteerhub.community.service.write_service.impl;

import com.volunteerhub.authentication.service.UserBanCacheService;
import com.volunteerhub.community.dto.rest.response.ModerationAction;
import com.volunteerhub.community.dto.rest.response.ModerationResponse;
import com.volunteerhub.community.dto.rest.response.ModerationResult;
import com.volunteerhub.community.dto.rest.response.ModerationStatus;
import com.volunteerhub.community.dto.rest.response.ModerationTargetType;
import com.volunteerhub.community.model.db_enum.UserStatus;
import com.volunteerhub.community.repository.RoleInEventRepository;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.community.service.write_service.IUserManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserManagerService implements IUserManagerService {
    private final UserProfileRepository userProfileRepository;
    private final RoleInEventRepository roleInEventRepository;
    private final UserBanCacheService userBanCacheService;

    @Override
    public ModerationResponse banUser(UUID userId) {
        int updated = userProfileRepository.updateStatus(userId, UserStatus.BANNED);

        if (updated == 0) {
            return ModerationResponse.failure(
                    ModerationAction.BAN_USER,
                    ModerationTargetType.USER,
                    userId.toString(),
                    ModerationResult.NOT_FOUND,
                    ModerationStatus.FAILED,
                    String.format("User with ID %s does not participation event", userId),
                    "USER_NOT_FOUND"
            );
        }

        userBanCacheService.markBanned(userId);

        return ModerationResponse.success(
                ModerationAction.BAN_USER,
                ModerationTargetType.USER,
                userId.toString(),
                ModerationStatus.BANNED,
                String.format("User %s has been banned", userId)
        );
    }

    @Override
    public ModerationResponse unbanUser(UUID userId) {
        int updated = userProfileRepository.updateStatus(userId, UserStatus.ACTIVE);

        if (updated == 0) {
            return ModerationResponse.failure(
                    ModerationAction.UNBAN_USER,
                    ModerationTargetType.USER,
                    userId.toString(),
                    ModerationResult.NOT_FOUND,
                    ModerationStatus.FAILED,
                    String.format("User with ID %s does not participation event", userId),
                    "USER_NOT_FOUND"
            );
        }

        userBanCacheService.markUnbanned(userId);

        return ModerationResponse.success(
                ModerationAction.UNBAN_USER,
                ModerationTargetType.USER,
                userId.toString(),
                ModerationStatus.UNBANNED,
                String.format("User %s has been unbanned", userId)
        );
    }
}
