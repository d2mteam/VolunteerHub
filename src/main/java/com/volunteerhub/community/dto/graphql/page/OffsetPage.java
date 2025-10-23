package com.volunteerhub.community.dto.graphql.page;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OffsetPage<T> {
    private List<T> content;
    private PageInfo pageInfo;
}