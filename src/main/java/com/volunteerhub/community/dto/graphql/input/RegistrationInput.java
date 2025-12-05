package com.volunteerhub.community.dto.graphql.input;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrationInput {
    @NotNull(message = "Event ID is required")
    private Long eventId;
}