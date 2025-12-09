package com.volunteerhub.community.dto.graphql.input;

import lombok.Data;

@Data
public class EventFilter {
    private Boolean recentlyCreated;
    private Boolean trending;
    private Integer limit;
    private String since;
}
