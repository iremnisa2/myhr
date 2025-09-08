package com.myhr.myhr.application.service;

import com.myhr.myhr.domain.exception.ApiException;
import com.myhr.myhr.domain.exception.ErrorCode;
import com.myhr.myhr.domain.CompanyStatus;
import com.myhr.myhr.infrastructure.entity.ApprovalTokenEntity;
import com.myhr.myhr.infrastructure.entity.CompanyEntity;

import com.myhr.myhr.infrastructure.repository.ApprovalTokenJpaRepository;
import com.myhr.myhr.infrastructure.repository.CompanyJpaRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyApprovalService {

    private final ApprovalTokenJpaRepository approvalRepo;
    private final CompanyJpaRepository companyRepo;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String approveAndCreateToken(Long companyId) {
        CompanyEntity company = companyRepo.findById(companyId)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.COMPANY_NOT_FOUND));



        CompanyStatus status = company.getStatus();
        switch (status) {
            case PENDING -> company.setStatus(CompanyStatus.APPROVED);
            case APPROVED -> {  }
            case ACTIVE -> throw new ApiException(ErrorCode.ALREADY_EXISTS);
        }

        companyRepo.save(company);


        String token = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plus(24, ChronoUnit.HOURS);

        ApprovalTokenEntity approval = ApprovalTokenEntity.builder()
                .token(token)
                .expiresAt(expiresAt)
                .used(false)
                .company(company)
                .build();

        approvalRepo.save(approval);


        try {
            mailService.sendSetPasswordMail(company.getEmail(), token);
        } catch (Exception e) {
            throw new ApiException(
                    ErrorCode.MAIL_NOT_SEND,
                    "E-posta gönderilemedi: " + rootCauseMessage(e),
                    e
            );
        }

        return token;
    }


    public void setPasswordWithToken(String token, String rawPassword) {
        var approval = approvalRepo.findByToken(token.trim())
                .orElseThrow(() -> new ApiException(
                        ErrorCode.TOKEN_NOT_FOUND));

        if (Boolean.TRUE.equals(approval.isUsed())) {
            throw new ApiException(ErrorCode.TOKEN_USED);
        }
        if (approval.getExpiresAt() != null && approval.getExpiresAt().isBefore(Instant.now())) {
            throw new ApiException(ErrorCode.TOKEN_EXPIRED);
        }

        CompanyEntity company = approval.getCompany();
        company.setPasswordHash(passwordEncoder.encode(rawPassword));
        companyRepo.save(company);

        approval.setUsed(true);
        approvalRepo.save(approval);
    }


    private static String rootCauseMessage(Throwable t) {
        Throwable r = t;
        while (r.getCause() != null) r = r.getCause();
        return r.getClass().getSimpleName() + ": " + String.valueOf(r.getMessage());
    }
}
