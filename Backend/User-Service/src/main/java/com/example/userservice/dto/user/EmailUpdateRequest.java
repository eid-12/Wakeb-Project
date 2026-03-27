package com.example.userservice.dto.user;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailUpdateRequest(
        @Nullable @Email String oldEmail,
        @Email @NotBlank String newEmail) {}
