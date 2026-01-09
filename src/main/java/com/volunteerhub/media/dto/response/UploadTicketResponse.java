package com.volunteerhub.media.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UploadTicketResponse {
    private UUID resourceId;
    private String uploadUrl;
    private String method;
    private int expiresInSeconds;
    private String contentType;
}
