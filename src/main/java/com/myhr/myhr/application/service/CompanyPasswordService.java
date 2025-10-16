package com.myhr.myhr.application.service;

import com.myhr.myhr.application.validation.PasswordPolicyValidator;
import com.myhr.myhr.domain.CompanyStatus;
import com.myhr.myhr.domain.exception.ApiException;
import com.myhr.myhr.domain.exception.ErrorCode;
import com.myhr.myhr.infrastructure.entity.ApprovalTokenEntity;
import com.myhr.myhr.infrastructure.entity.CompanyEntity;
import com.myhr.myhr.infrastructure.repository.ApprovalTokenJpaRepository;
import com.myhr.myhr.infrastructure.repository.CompanyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CompanyPasswordService {

    private final ApprovalTokenJpaRepository approvalTokenRepo;
    private final CompanyJpaRepository companyRepo;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void setPassword(String token, String rawPassword) {
        String t = token == null ? null : token.trim();

        ApprovalTokenEntity at = approvalTokenRepo.findByToken(t)
                .orElseThrow(() -> new ApiException(ErrorCode.TOKEN_NOT_FOUND));

        if (Boolean.TRUE.equals(at.isUsed())) {
            throw new ApiException(ErrorCode.TOKEN_USED);
        }
        if (at.getExpiresAt() == null || at.getExpiresAt().isBefore(Instant.now())) {
            throw new ApiException(ErrorCode.TOKEN_EXPIRED);
        }

        var policyErrors = PasswordPolicyValidator.validate(rawPassword);
        if (!policyErrors.isEmpty()) {
            throw new ApiException(ErrorCode.PASSWORD_TOO_WEAK);
        }

        CompanyEntity company = at.getCompany();
        company.setPasswordHash(passwordEncoder.encode(rawPassword));
        company.setStatus(CompanyStatus.ACTIVE);
        companyRepo.save(company);


        int updated = approvalTokenRepo.markUsedIfNotUsed(t);
        if (updated == 0) {
            throw new ApiException(ErrorCode.TOKEN_USED, "Token zaten kullanılmış.");
        }
    }
}
