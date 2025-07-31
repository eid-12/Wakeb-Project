package com.example.placeservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
public record PlaceResponse(
        Integer id,
        String name,
        String description,
        BigDecimal latitude,
        BigDecimal longitude,
        String imagePath,
        Timestamp createdAt,
        String category,
        Integer userId
) {}

