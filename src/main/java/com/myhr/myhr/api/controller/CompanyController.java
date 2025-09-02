package com.myhr.myhr.api.controller;

import com.myhr.myhr.application.service.CompanyService;
import com.myhr.myhr.api.dto.CompanyApplyRequest;
import com.myhr.myhr.api.dto.CompanyResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("/apply")
    public ResponseEntity<CompanyResponse> apply(@RequestBody @Valid CompanyApplyRequest request) {
        CompanyResponse resp = companyService.apply(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }
}
