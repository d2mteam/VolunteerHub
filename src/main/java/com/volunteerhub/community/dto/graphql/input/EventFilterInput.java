package com.volunteerhub.community.dto.graphql.input;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventFilterInput {
    private String keyword;
    private List<String> categories;
    private LocalDateTime startDateFrom;
    private LocalDateTime startDateTo;
    private String location;
    private String eventState;
}
