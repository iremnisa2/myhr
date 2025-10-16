package com.myhr.myhr.api.controller;

import com.myhr.myhr.application.service.CompanyApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyApprovalController {

    private final CompanyApprovalService approvalService;

    @RestController
    @RequestMapping("/api/admin")
    @lombok.RequiredArgsConstructor
    public class AdminCompanyApi {
        private final com.myhr.myhr.application.service.CompanyApprovalService approvalService;

        @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
        @PostMapping("/companies/{id}/approve")
        public java.util.Map<String,Object> approve(@PathVariable Long id) {
            String token = approvalService.approveAndCreateToken(id);
            return java.util.Map.of("status","ok","companyId",id,"setPasswordToken",token);
        }
    }

}


