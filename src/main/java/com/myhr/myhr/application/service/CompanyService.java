package com.myhr.myhr.application.service;

import com.myhr.myhr.api.dto.CompanyApplyRequest;
import com.myhr.myhr.api.dto.CompanyResponse;
import com.myhr.myhr.domain.CompanyStatus;
import com.myhr.myhr.domain.exception.ApiException;
import com.myhr.myhr.domain.exception.ErrorCode;
import com.myhr.myhr.infrastructure.entity.ApprovalTokenEntity;
import com.myhr.myhr.infrastructure.entity.CompanyEntity;
import com.myhr.myhr.infrastructure.repository.ApprovalTokenJpaRepository;
import com.myhr.myhr.infrastructure.repository.CompanyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyJpaRepository companyRepo;
    private final ApprovalTokenJpaRepository approvalTokenRepo;
    private final MailService mailService;

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

        var saved = companyRepo.save(company);

        var token = UUID.randomUUID().toString();
        var expiresAt = Instant.now().plus(24, ChronoUnit.HOURS);

        var approval = ApprovalTokenEntity.builder()
                .token(token)
                .expiresAt(expiresAt)
                .used(false)
                .company(saved)
                .build();

        approvalTokenRepo.save(approval);


        return new CompanyResponse(
                saved.getId(), saved.getName(), saved.getEmail(),
                saved.getPhone(), saved.getEmployeeCount(),
                saved.getStatus(), saved.getCreatedAt().toString()
        );
    }
}
