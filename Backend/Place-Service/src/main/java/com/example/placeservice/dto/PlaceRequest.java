package com.example.placeservice.dto;


import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

public record PlaceRequest(
        String name,
        String description,
        String category,
        BigDecimal latitude,
        BigDecimal longitude

) {}
