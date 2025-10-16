package com.myhr.myhr.api.dto;

import com.myhr.myhr.domain.Role;

public record UserResponse(
        Long id,
        String email,
        Role role,
        boolean active
) {}