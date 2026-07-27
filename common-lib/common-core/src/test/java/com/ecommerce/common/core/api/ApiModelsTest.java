package com.ecommerce.common.core.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApiModelsTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void serializesSuccessfulResponse() throws Exception {
        Instant timestamp = Instant.parse("2026-07-25T10:15:30Z");
        ApiResponse<Map<String, String>> response =
                new ApiResponse<>(true, Map.of("id", "product-1"), null, timestamp);

        assertThat(objectMapper.readTree(objectMapper.writeValueAsString(response)))
                .isEqualTo(objectMapper.readTree("""
                        {
                          "success": true,
                          "data": {"id": "product-1"},
                          "timestamp": "2026-07-25T10:15:30Z"
                        }
                        """));
    }

    @Test
    void rejectsFalseSuccessResponse() {
        assertThatThrownBy(() -> new ApiResponse<>(false, null, null, Instant.now()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void serializesErrorWithoutLeakingRejectedValues() throws Exception {
        Instant timestamp = Instant.parse("2026-07-25T10:15:30Z");
        ApiError error = new ApiError(
                "VALIDATION_ERROR",
                "Validation failed",
                HttpStatus.BAD_REQUEST.value(),
                "/api/products",
                timestamp,
                List.of(new FieldError("name", "Name is required"))
        );

        assertThat(objectMapper.readTree(objectMapper.writeValueAsString(error)))
                .isEqualTo(objectMapper.readTree("""
                        {
                          "code": "VALIDATION_ERROR",
                          "message": "Validation failed",
                          "status": 400,
                          "path": "/api/products",
                          "timestamp": "2026-07-25T10:15:30Z",
                          "fieldErrors": [
                            {"field": "name", "message": "Name is required"}
                          ]
                        }
                        """));
    }

    @Test
    void defensivelyCopiesErrorFields() {
        List<FieldError> source = new ArrayList<>();
        source.add(new FieldError("name", "Name is required"));

        ApiError error = new ApiError(
                "VALIDATION_ERROR",
                "Validation failed",
                400,
                "/api/products",
                Instant.now(),
                source
        );
        source.clear();

        assertThat(error.fieldErrors()).hasSize(1);
        assertThatThrownBy(() -> error.fieldErrors().add(new FieldError("price", "Invalid price")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void rejectsNonErrorHttpStatus() {
        assertThatThrownBy(() -> new ApiError(
                "OK",
                "Not an error",
                200,
                "/api/products",
                Instant.now(),
                List.of()
        )).isInstanceOf(IllegalArgumentException.class);
    }
}
