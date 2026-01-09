package com.volunteerhub.media.dto.response;

import com.volunteerhub.media.model.MediaStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ConfirmUploadResponse {
    private UUID resourceId;
    private MediaStatus status;
    private String contentType;
    private Long sizeBytes;
    private String checksum;
}
