package com.myhr.myhr.infrastructure.repository;

import com.myhr.myhr.infrastructure.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyJpaRepository extends JpaRepository<CompanyEntity, Long> {
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    Optional<CompanyEntity> findByApprovalToken(String approvalToken);
}
