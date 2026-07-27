package com.ecommerce.userservice.dto;

import com.ecommerce.userservice.entity.Gender;
import com.ecommerce.userservice.validation.HttpUrl;
import com.ecommerce.userservice.validation.TrimmedSize;
import com.ecommerce.userservice.validation.VietnamesePhone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        // @NotBlank(message = "Full name must not be blank")
        // @TrimmedSize(min = 3, max = 100,
        //         message = "Full name must be between 3 and 100 characters after trimming")
        String fullName,

        // @NotBlank(message = "Username must not be blank")
        // @TrimmedSize(min = 3, max = 100,
                // message = "Username must be between 3 and 100 characters after trimming")
        String username,

        // @NotBlank(message = "Email must not be blank")
        // @Size(max = 254, message = "Email must not exceed 254 characters")
        // @Email(message = "Email must be valid")
        String email,

        // @NotNull(message = "Gender must not be null")
        Gender gender,

        // @VietnamesePhone
        String phone,

        // @Size(max = 2048, message = "Avatar URL must not exceed 2048 characters")
        @HttpUrl
        String avatar
) {
}
