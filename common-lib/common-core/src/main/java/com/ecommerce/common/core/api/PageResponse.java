package com.ecommerce.common.core.api;

import java.util.List;
import java.util.Objects;

public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {

    public PageResponse {
        content = List.copyOf(Objects.requireNonNull(content, "content must not be null"));
        if (page < 0) {
            throw new IllegalArgumentException("page must be zero or greater");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("size must be greater than zero");
        }
        if (totalElements < 0) {
            throw new IllegalArgumentException("totalElements must be zero or greater");
        }
        if (totalPages < 0) {
            throw new IllegalArgumentException("totalPages must be zero or greater");
        }

        int expectedTotalPages = Math.toIntExact(Math.ceilDiv(totalElements, size));
        if (totalPages != expectedTotalPages) {
            throw new IllegalArgumentException("totalPages does not match totalElements and size");
        }
        if (totalPages == 0 && page != 0) {
            throw new IllegalArgumentException("an empty result only supports page zero");
        }
        if (totalPages > 0 && page >= totalPages) {
            throw new IllegalArgumentException("page must be lower than totalPages");
        }
        if (first != (page == 0)) {
            throw new IllegalArgumentException("first does not match page");
        }
        boolean expectedLast = totalPages == 0 || page == totalPages - 1;
        if (last != expectedLast) {
            throw new IllegalArgumentException("last does not match page and totalPages");
        }
    }

    public static <T> PageResponse<T> of(
            List<T> content,
            int page,
            int size,
            long totalElements
    ) {
        if (size <= 0) {
            throw new IllegalArgumentException("size must be greater than zero");
        }
        int totalPages = Math.toIntExact(Math.ceilDiv(totalElements, size));
        boolean first = page == 0;
        boolean last = totalPages == 0 || page == totalPages - 1;
        return new PageResponse<>(content, page, size, totalElements, totalPages, first, last);
    }
}
