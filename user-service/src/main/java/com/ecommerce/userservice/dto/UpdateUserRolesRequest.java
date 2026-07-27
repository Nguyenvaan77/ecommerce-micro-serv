package com.ecommerce.userservice.dto;

import com.ecommerce.userservice.entity.RoleName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record UpdateUserRolesRequest(
        @NotEmpty(message = "At least one role is required")
        Set<@Valid @NotNull(message = "Role must not be null") RoleName> roles
) {
}
