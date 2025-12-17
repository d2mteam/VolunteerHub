package com.volunteerhub.community.model.graphql;

import lombok.Data;

import java.util.List;

@Data
public class PostFilterInput {
    private Boolean recent;
    private Integer limit;
    private List<Long> eventIds;
    private String since;
    private Boolean trending;
}
