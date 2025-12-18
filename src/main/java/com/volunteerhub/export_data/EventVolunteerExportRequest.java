package com.volunteerhub.export_data;

import com.volunteerhub.community.model.db_enum.EventRole;
import com.volunteerhub.community.model.db_enum.EventState;
import com.volunteerhub.community.model.db_enum.ParticipationStatus;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Jacksonized
public class EventVolunteerExportRequest {
    @NotEmpty(message = "At least one field is required")
    @Builder.Default
    private List<String> fields = new ArrayList<>();

    @Builder.Default
    private String format = "json";

    private List<Long> eventIds;

    private List<EventRole> eventRoles;

    private List<ParticipationStatus> participationStatuses;

    private List<EventState> eventStates;
}
