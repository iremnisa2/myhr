package com.myhr.myhr.application.service;

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

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CompanyPasswordService {

    private final ApprovalTokenJpaRepository approvalTokenRepo;
    private final CompanyJpaRepository companyRepo;
    private final PasswordEncoder passwordEncoder;

    public void setPassword(String token, String rawPassword) {
        String t = token == null ? null : token.trim();

        ApprovalTokenEntity at = approvalTokenRepo.findByToken(t)
                .orElseThrow(() -> new ApiException(ErrorCode.TOKEN_NOT_FOUND));
        if (at.getExpiresAt() == null || at.getExpiresAt().isBefore(Instant.now())) {
            throw new ApiException(ErrorCode.TOKEN_EXPIRED);
        }

        CompanyEntity company = at.getCompany();

        if (!rawPassword.matches("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")) {
            throw new ApiException(ErrorCode.PASSWORD_TOO_WEAK);
        }

        company.setPasswordHash(passwordEncoder.encode(rawPassword));

         company.setStatus(CompanyStatus.APPROVED);

        companyRepo.save(company);


        approvalTokenRepo.delete(at);
    }
}
