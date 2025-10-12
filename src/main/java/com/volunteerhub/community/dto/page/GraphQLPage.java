package com.volunteerhub.community.dto.page;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GraphQLPage<T> {
    private List<T> content;
    private PageInfo pageInfo;
}