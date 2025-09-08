package com.myhr.myhr.api.dto;

import com.myhr.myhr.domain.CompanyStatus;

import java.time.Instant;

public record CompanyResponse(
        Long id,
        String name,
        String email,
        String phone,
        int employeeCount,
        CompanyStatus status,
        Instant createdAt
) {}

