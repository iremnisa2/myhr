package com.myhr.myhr.application.service;

import com.myhr.myhr.api.dto.CompanyApplyRequest;
import com.myhr.myhr.api.dto.CompanyResponse;
import com.myhr.myhr.domain.CompanyStatus;
import com.myhr.myhr.domain.exception.ApiException;
import com.myhr.myhr.domain.exception.ErrorCode;
import com.myhr.myhr.infrastructure.entity.CompanyEntity;
import com.myhr.myhr.infrastructure.repository.CompanyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyJpaRepository companyRepo;

    @Transactional
    public CompanyResponse apply(CompanyApplyRequest req) {
        if (companyRepo.existsByEmail(req.email())) throw new ApiException(ErrorCode.EMAIL_ALREADY_EXISTS);
        if (companyRepo.existsByPhone(req.phone())) throw new ApiException(ErrorCode.PHONE_ALREADY_EXISTS);

        var company = CompanyEntity.builder()
                .name(req.name())
                .email(req.email())
                .phone(req.phone())
                .employeeCount(req.employeeCount())
                .status(CompanyStatus.PENDING)
                .build();

        companyRepo.saveAndFlush(company);

        return new CompanyResponse(
                company.getId(), company.getName(), company.getEmail(),
                company.getPhone(), company.getEmployeeCount(),
                company.getStatus(),
                company.getCreatedAt() != null ? company.getCreatedAt().toString() : Instant.now().toString()
        );
    }
}
