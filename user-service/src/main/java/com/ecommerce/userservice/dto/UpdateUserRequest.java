package com.ecommerce.userservice.dto;

import com.ecommerce.userservice.entity.Gender;
import com.ecommerce.userservice.validation.HttpUrl;
import com.ecommerce.userservice.validation.TrimmedSize;
import com.ecommerce.userservice.validation.VietnamesePhone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        // @NotBlank(message = "Full name must not be blank")
        // @TrimmedSize(min = 3, max = 100,
        //         message = "Full name must be between 3 and 100 characters after trimming")
        String fullName,

        // @NotNull(message = "Gender must not be null")
        Gender gender,

        // @VietnamesePhone
        String phone,

        // @Size(max = 2048, message = "Avatar URL must not exceed 2048 characters")
        // @HttpUrl
        String avatar
) {
}
