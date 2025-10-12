package com.volunteerhub.authentication.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class RegistrationResponse {
    private UUID userId;
}
