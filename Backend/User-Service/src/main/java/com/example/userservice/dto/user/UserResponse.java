package com.example.userservice.dto.user;

import java.util.Set;

public record UserResponse(Integer id, String name, String language,
                           String theme, String fontSize, boolean locationTracking , Boolean isUser , String email , Boolean active) {}
