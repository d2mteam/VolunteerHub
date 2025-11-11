package com.volunteerhub.community.dto.graphql.input;

import lombok.Data;

import java.util.Map;

@Data
public class RegistrationInput {
    private Long eventId;
    private Map<String, Object> extraInfos;
}
