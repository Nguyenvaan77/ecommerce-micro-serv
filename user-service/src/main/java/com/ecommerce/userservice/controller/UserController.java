package com.ecommerce.userservice.controller;

import com.ecommerce.common.core.api.ApiResponse;
import com.ecommerce.common.core.api.PageResponse;
import com.ecommerce.userservice.dto.CreateUserRequest;
import com.ecommerce.userservice.dto.UpdateUserRequest;
import com.ecommerce.userservice.dto.UserResponse;
import com.ecommerce.userservice.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(
            @Valid @RequestBody CreateUserRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(userService.create(request)));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getById(
            @PathVariable @Positive(message = "User id must be positive") Long id
    ) {
        return ApiResponse.success(userService.getById(id));
    }

    @GetMapping
    public ApiResponse<PageResponse<UserResponse>> getUsers(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page must be zero or greater")
            int page,
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "Size must be at least 1")
            @Max(value = 100, message = "Size must not exceed 100")
            int size,
            @RequestParam(required = false)
            String keyword
    ) {
        return ApiResponse.success(userService.getUsers(page, size, keyword));
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> update(
            @PathVariable @Positive(message = "User id must be positive") Long id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        return ApiResponse.success(userService.update(id, request));
    }

}
