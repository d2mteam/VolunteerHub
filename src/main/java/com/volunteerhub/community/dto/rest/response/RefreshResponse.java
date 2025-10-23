package com.volunteerhub.community.dto.rest.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RefreshResponse {
    private String accessToken;
}
