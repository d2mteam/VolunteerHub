package com.volunteerhub.ultis.page;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageInfo {
    private int page;
    private int size;
    private int totalElements;
    private int totalPages;
    private int hasNext;
    private int hasPrevious;
}
