package com.ecommerce.common.core.api;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PageResponseTest {

    @Test
    void calculatesZeroBasedPageMetadata() {
        PageResponse<String> page = PageResponse.of(List.of("a", "b"), 1, 2, 5);

        assertThat(page.page()).isEqualTo(1);
        assertThat(page.size()).isEqualTo(2);
        assertThat(page.totalElements()).isEqualTo(5);
        assertThat(page.totalPages()).isEqualTo(3);
        assertThat(page.first()).isFalse();
        assertThat(page.last()).isFalse();
    }

    @Test
    void calculatesEmptyPageMetadata() {
        PageResponse<String> page = PageResponse.of(List.of(), 0, 20, 0);

        assertThat(page.totalPages()).isZero();
        assertThat(page.first()).isTrue();
        assertThat(page.last()).isTrue();
    }

    @Test
    void defensivelyCopiesContent() {
        List<String> source = new ArrayList<>(List.of("item"));
        PageResponse<String> page = PageResponse.of(source, 0, 10, 1);
        source.clear();

        assertThat(page.content()).containsExactly("item");
        assertThatThrownBy(() -> page.content().add("another"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void rejectsInvalidPaginationValues() {
        assertThatThrownBy(() -> PageResponse.of(List.of(), -1, 10, 0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PageResponse.of(List.of(), 0, 0, 0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PageResponse.of(List.of(), 0, 10, -1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PageResponse.of(List.of(), 2, 10, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsInconsistentMetadata() {
        assertThatThrownBy(() -> new PageResponse<>(
                List.of("item"),
                0,
                10,
                1,
                2,
                true,
                false
        )).isInstanceOf(IllegalArgumentException.class);
    }
}
