package com.example.savedplaceservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;


public record AddSavedPlaceRequest(
        @NotBlank String title,
        @NotNull BigDecimal latitude,
        @NotNull   BigDecimal longitude) {}