package com.volunteerhub.community.dto.rest.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshRequest {
    @NotEmpty
    private String refreshToken;
}
