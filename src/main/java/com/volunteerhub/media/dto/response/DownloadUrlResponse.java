package com.volunteerhub.media.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DownloadUrlResponse {
    private String downloadUrl;
    private String method;
    private int expiresInSeconds;
}
