package com.myhr.myhr.infrastructure.repository;

import com.myhr.myhr.infrastructure.entity.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountJpaRepository extends JpaRepository<UserAccountEntity, Long> {
    Optional<UserAccountEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
