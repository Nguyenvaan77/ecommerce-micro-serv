package com.ecommerce.userservice.mapper;

import com.ecommerce.userservice.dto.CreateUserRequest;
import com.ecommerce.userservice.dto.UpdateUserRequest;
import com.ecommerce.userservice.dto.UserResponse;
import com.ecommerce.userservice.entity.Role;
import com.ecommerce.userservice.entity.RoleName;
import com.ecommerce.userservice.entity.User;
import com.ecommerce.userservice.validation.UserInputNormalizer;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    private final UserInputNormalizer normalizer;

    public UserMapper(UserInputNormalizer normalizer) {
        this.normalizer = normalizer;
    }

    public User toEntity(CreateUserRequest request) {
        return User.builder()
                .fullName(normalizer.requiredText(request.fullName()))
                .username(normalizer.username(request.username()))
                .email(normalizer.email(request.email()))
                .gender(request.gender())
                .phone(normalizer.phone(request.phone()))
                .avatar(normalizer.optionalText(request.avatar()))
                .build();
    }

    public void updateEntity(User user, UpdateUserRequest request) {
        user.setFullName(normalizer.requiredText(request.fullName()));
        user.setGender(request.gender());
        user.setPhone(normalizer.phone(request.phone()));
        user.setAvatar(normalizer.optionalText(request.avatar()));
    }

    public UserResponse toResponse(User user) {
        Set<RoleName> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                user.getGender(),
                user.getPhone(),
                user.getAvatar(),
                roles
        );
    }
}
