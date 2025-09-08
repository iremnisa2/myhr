package com.myhr.myhr.api.controller;

import com.myhr.myhr.application.service.CompanyApprovalService;
import com.myhr.myhr.domain.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyApprovalController {

    private final CompanyApprovalService approvalService;


    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id) {
        String token = approvalService.approveAndCreateToken(id);


        return ResponseEntity.ok(Map.of(
                "message", "Approval mail sent.",
                "token", token
        ));
    }


}

