package com.volunteerhub.community.service;

import com.volunteerhub.community.dto.input.EditEventInput;
import com.volunteerhub.community.dto.input.EditUserProfileInput;
import com.volunteerhub.community.dto.output.UserProfileDto;

public interface UserProfileService {
    UserProfileDto getUserProfile(Long id);
    UserProfileDto editUserProfile(EditUserProfileInput input);
}
