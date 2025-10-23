package com.volunteerhub.community.dto.graphql.page;

import org.springframework.data.domain.Page;

public class PageUtils {
    public static PageInfo from(Page<?> page) {
        return PageInfo.builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements((int) page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext() ? 1 : 0)
                .hasPrevious(page.hasPrevious() ? 1 : 0)
                .build();
    }
}