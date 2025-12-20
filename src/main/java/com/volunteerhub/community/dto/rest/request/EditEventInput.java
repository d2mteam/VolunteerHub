package com.volunteerhub.community.dto.rest.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class EditEventInput {
    @NotNull(message = "Event ID is required")
    private Long eventId;

    @NotBlank(message = "Event name is required")
    @Size(max = 200, message = "Event name max 200 chars")
    private String eventName;

    @NotBlank(message = "Event description is required")
    @Size(max = 500, message = "Event description max 500 chars")
    private String eventDescription;

    @NotBlank(message = "Event location is required")
    @Size(max = 200, message = "Event location max 200 chars")
    private String eventLocation;

    private List<String> categories;
}
