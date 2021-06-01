package com.drawsforall.user.management.web.rest.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

@Builder
@Getter
@ToString
public class PagedUserAllQueriesDTO {

    private final List<UserDTO> elements;

    @Accessors(fluent = true)
    private final boolean hasNext;

    @Accessors(fluent = true)
    private final boolean hasPrevious;

    private final int numberOfElements;

    private final int currentPage;

    private final int size;

    private final long totalElements;

    private final int totalPages;

    private final String sort;

    private final String by;

    private final String filterBy;

    private final String dateTypeSearch;

    private final String dateIn;

    private final String dateOut;
}
