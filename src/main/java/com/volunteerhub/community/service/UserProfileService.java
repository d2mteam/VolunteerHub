package com.volunteerhub.community.service;

import com.volunteerhub.community.dto.output.UserProfileDto;

public interface UserProfileService {
    UserProfileDto getUserProfile(Long id);
}
