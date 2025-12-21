package com.volunteerhub.community.readmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileSummaryView implements Serializable {
    private UUID userId;
    private String username;
    private String fullName;
    private String avatarId;
}
