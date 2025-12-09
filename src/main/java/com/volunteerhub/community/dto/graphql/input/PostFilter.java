package com.volunteerhub.community.dto.graphql.input;

import lombok.Data;

import java.util.List;

@Data
public class PostFilter {
    private Boolean recent;
    private Integer limit;
    private List<Long> eventIds;
}
