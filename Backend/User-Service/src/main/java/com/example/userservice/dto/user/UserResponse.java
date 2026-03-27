package com.example.userservice.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserResponse(
        Integer id,
        String name,
        String language,
        String theme,
        String fontSize,
        boolean locationTracking,
        @JsonProperty("isUser") Boolean isUser,
        String email,
        Boolean active) {}
