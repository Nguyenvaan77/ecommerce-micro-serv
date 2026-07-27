package com.ecommerce.userservice.dto;

import com.ecommerce.userservice.entity.Gender;
import com.ecommerce.userservice.entity.RoleName;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public record UserResponse(
        Long id,
        String fullName,
        String username,
        String email,
        Gender gender,
        String phone,
        String avatar,
        Set<RoleName> roles
) {

    public UserResponse {
        Objects.requireNonNull(roles, "roles must not be null");
        roles = Collections.unmodifiableSet(new LinkedHashSet<>(roles));
    }
}
