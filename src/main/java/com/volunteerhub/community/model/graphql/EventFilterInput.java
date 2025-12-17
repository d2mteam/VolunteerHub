package com.volunteerhub.community.model.graphql;

import lombok.Data;

@Data
public class EventFilterInput {
    private Boolean recentlyCreated;
    private Boolean trending;
    private Integer limit;
    private String since;
}
