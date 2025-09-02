package com.myhr.myhr.infrastructure.entity;

import com.myhr.myhr.domain.CompanyStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "companies",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_company_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_company_phone", columnNames = "phone")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=100)
    private String name;

    @Column(nullable=false, length=254)
    private String email;

    @Column(nullable=false, length=20)
    private String phone;

    @Column(nullable=false)
    private Integer employeeCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=16)
    private CompanyStatus status;

    @Column(length=200)
    private String passwordHash;


    @CreationTimestamp
    @Column(nullable=false, updatable=false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable=false)
    private Instant updatedAt;
}
