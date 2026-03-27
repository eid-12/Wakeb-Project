package com.example.userservice.dto.user;

import jakarta.validation.constraints.Size;

public record PhoneUpdateRequest(@Size(max = 30) String phone) {}
