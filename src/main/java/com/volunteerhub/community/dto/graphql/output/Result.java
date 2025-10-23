package com.volunteerhub.community.dto.graphql.output;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Result {
    private String state;
    private String message;
}
