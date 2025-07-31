package com.example.authservice.dto;//package com.example.userservice.dto.user;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public record PasswordChangeRequest(
        @NotNull @JsonAlias({"userId"}) Integer userId,
      @NotBlank  @JsonAlias({"current"}) String oldPassword,
      @NotBlank @JsonAlias({"_new","new"})   String newPassword) {}
