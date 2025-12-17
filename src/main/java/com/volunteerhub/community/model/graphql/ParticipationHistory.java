package com.volunteerhub.community.model.graphql;

import com.volunteerhub.community.model.db_enum.EventRole;
import com.volunteerhub.community.model.db_enum.ParticipationStatus;
import com.volunteerhub.community.model.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ParticipationHistory {
    private final Event event;
    private final EventRole eventRole;
    private final ParticipationStatus participationStatus;
    private final LocalDateTime joinedAt;
    private final LocalDateTime updatedAt;
}
