package com.ecommerce.userservice.controller;

import com.ecommerce.common.core.api.ApiResponse;
import com.ecommerce.userservice.dto.UpdateUserRolesRequest;
import com.ecommerce.userservice.dto.UserResponse;
import com.ecommerce.userservice.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@ConditionalOnProperty(
        prefix = "user-service",
        name = "local-role-management-enabled",
        havingValue = "true"
)
public class LocalUserRoleController {

    private final UserService userService;

    public LocalUserRoleController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{id}/roles")
    public ApiResponse<UserResponse> updateRoles(
            @PathVariable @Positive(message = "User id must be positive") Long id,
            @Valid @RequestBody UpdateUserRolesRequest request
    ) {
        return ApiResponse.success(userService.updateRoles(id, request));
    }
}
