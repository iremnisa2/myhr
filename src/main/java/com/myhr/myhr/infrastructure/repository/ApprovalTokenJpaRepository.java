package com.myhr.myhr.infrastructure.repository;

import com.myhr.myhr.infrastructure.entity.ApprovalTokenEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ApprovalTokenJpaRepository extends JpaRepository<ApprovalTokenEntity, Long> {

    Optional<ApprovalTokenEntity> findByToken(String token);
    boolean existsByToken(String token);
    void deleteByToken(String token);

    @Modifying
    @Query("update ApprovalTokenEntity t set t.used = true where t.token = :token and t.used = false")
    int markUsedIfNotUsed(@Param("token") String token);
}
