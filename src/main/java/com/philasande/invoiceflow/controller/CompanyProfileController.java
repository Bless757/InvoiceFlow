package com.philasande.invoiceflow.controller;

import com.philasande.invoiceflow.entity.CompanyProfile;
import com.philasande.invoiceflow.entity.User;
import com.philasande.invoiceflow.repository.CompanyProfileRepository;
import com.philasande.invoiceflow.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company-profile")
@RequiredArgsConstructor
public class CompanyProfileController {

    private final CompanyProfileRepository companyProfileRepository;

    @PostMapping
    public ResponseEntity<CompanyProfile> createCompanyProfile(@RequestBody CompanyProfile profile) {
        User currentUser = SecurityUtils.getCurrentUser();
        profile.setUser(currentUser);
        CompanyProfile saved = companyProfileRepository.save(profile);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<CompanyProfile> getMyCompanyProfile() {
        User currentUser = SecurityUtils.getCurrentUser();
        return companyProfileRepository.findByUserId(currentUser.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
