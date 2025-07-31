package com.example.authservice.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.*;

public record UsernameChangeRequest(
        @NotNull @JsonAlias({"userId"}) Integer userId,
        @NotBlank @Size(min = 3, max = 30)
        @Pattern(regexp = "^[A-Za-z0-9_\\.]+$", message = "username can only contain letters, numbers, underscore and dot")
        @JsonAlias({"newUsername"})   String newUsername
) {}
