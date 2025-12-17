package com.volunteerhub.community.model.graphql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ExportPayload {
    private final String filename;
    private final String contentType;
    private final String data;
}
