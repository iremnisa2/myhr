package com.myhr.myhr.infrastructure.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.Instant;

@Entity
@Table(
        name = "approval_tokens",
        uniqueConstraints = @UniqueConstraint(name = "uk_approval_token_token", columnNames = "token"),
        indexes = @Index(name = "idx_approval_token_company", columnList = "company_id")
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ApprovalTokenEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=64)
    private String token;

    @Column(nullable=false)
    private Instant expiresAt;

    @Column(nullable=false)
    private boolean used = false;

    @CreationTimestamp
    @Column(nullable=false, updatable=false)
    private Instant createdAt;

    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="company_id", nullable=false)
    private CompanyEntity company;
}
