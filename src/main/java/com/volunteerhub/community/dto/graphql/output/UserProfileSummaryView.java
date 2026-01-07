package com.volunteerhub.community.dto.graphql.output;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Builder
public class UserProfileSummaryView implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID userId;
    private String username;
    private String fullName;
    private String avatarId;
}
