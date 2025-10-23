package com.volunteerhub.community.dto.rest.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class RegistrationResponse {
    private UUID userId;
}
