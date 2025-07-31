package com.example.searchservice.dto;

import jakarta.validation.constraints.NotBlank;

public record SearchHistoryRequest(@NotBlank String query) {}


