package com.example.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record  LoginRequest (
        @NotBlank String username,
        @NotBlank @Size(min = 6) String password
){}
