package com.myhr.myhr.infrastructure.repository;

import com.myhr.myhr.infrastructure.entity.ApprovalTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApprovalTokenJpaRepository extends JpaRepository<ApprovalTokenEntity, Long> {
    Optional<ApprovalTokenEntity> findByToken(String token);
    boolean existsByToken(String token);
    void deleteByToken(String token);
}
