package com.example.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** Current session from Auth DB table {@code users} — {@code isUser false} = admin. */
@Getter
@AllArgsConstructor
public class AuthMeResponse {
    private final Integer id;
    private final String username;
    @JsonProperty("isUser")
    private final Boolean isUser;
}
