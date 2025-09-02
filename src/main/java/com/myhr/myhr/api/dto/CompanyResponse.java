package com.myhr.myhr.api.dto;

import com.myhr.myhr.domain.CompanyStatus;

public record CompanyResponse(
        Long id,
        String name,
        String email,
        String phone,
        int employeeCount,
        CompanyStatus status,
        String createdAt
) {}

