package com.volunteerhub.media.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ConfirmUploadRequest {
    @NotNull
    private UUID resourceId;
}
