package com.example.userservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailUpdateRequest(
        @Email String oldEmail,
        @Email @NotBlank String newEmail) {}
