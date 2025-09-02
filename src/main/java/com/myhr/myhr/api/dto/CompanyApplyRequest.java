package com.myhr.myhr.api.dto;

import jakarta.validation.constraints.*;

public record CompanyApplyRequest(
        @NotBlank @Size(min = 2, max = 100) String name,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Pattern(regexp = "^\\+[1-9]\\d{7,14}$", message = "Telefon E.164 formatında olmalı, örn: +905xxxxxxxxx")
        String phone,
        @Min(1) int employeeCount
) {}

