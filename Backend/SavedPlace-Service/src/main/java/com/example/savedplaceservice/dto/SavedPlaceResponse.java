package com.example.savedplaceservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

public record SavedPlaceResponse(
        Integer id,
        String  title,
        BigDecimal  latitude,
        BigDecimal longitude,
        Instant createdAt) {}
