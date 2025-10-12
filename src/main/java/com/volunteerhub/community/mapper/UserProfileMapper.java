package com.volunteerhub.community.mapper;

import com.volunteerhub.community.dto.output.UserProfileDto;
import com.volunteerhub.community.entity.UserProfile;

public class UserProfileMapper {
    public static UserProfile toEntity(UserProfileDto dto) {
        return UserProfile.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .createdAt(dto.getCreatedAt())
                .build();
    }

    public static UserProfileDto toDto(UserProfile entity) {
        return UserProfileDto.builder()
                .username(entity.getUsername())
                .email(entity.getEmail())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
