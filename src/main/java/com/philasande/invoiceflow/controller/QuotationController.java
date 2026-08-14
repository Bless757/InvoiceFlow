package com.philasande.invoiceflow.controller;

import com.philasande.invoiceflow.dto.QuotationRequestDto;
import com.philasande.invoiceflow.dto.QuotationResponseDto;
import com.philasande.invoiceflow.entity.User;
import com.philasande.invoiceflow.security.SecurityUtils;
import com.philasande.invoiceflow.service.EmailService;
import com.philasande.invoiceflow.service.PdfGeneratorService;
import com.philasande.invoiceflow.service.QuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quotations")
@RequiredArgsConstructor
public class QuotationController {

    private final QuotationService quotationService;
    private final PdfGeneratorService pdfGeneratorService;
    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<QuotationResponseDto> createQuotation(@RequestBody QuotationRequestDto dto) {
        User currentUser = SecurityUtils.getCurrentUser();
        QuotationResponseDto response = quotationService.createQuotation(dto, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<QuotationResponseDto>> getMyQuotations() {
        User currentUser = SecurityUtils.getCurrentUser();
        List<QuotationResponseDto> quotations = quotationService.getAllQuotations(currentUser);
        return ResponseEntity.ok(quotations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuotationResponseDto> getQuotation(@PathVariable Long id) {
        User currentUser = SecurityUtils.getCurrentUser();
        QuotationResponseDto quotation = quotationService.getQuotationById(id, currentUser);
        return ResponseEntity.ok(quotation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuotation(@PathVariable Long id) {
        User currentUser = SecurityUtils.getCurrentUser();
        quotationService.deleteQuotation(id, currentUser);
        return ResponseEntity.ok("Quotation deleted successfully");
    }

    // Temporary: PDF and Email are commented out until we update the services
    // We will fix them properly next

    /*
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadQuotationPdf(@PathVariable Long id) {
        // Will be fixed after updating PdfGeneratorService
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<String> sendQuotation(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        // Will be fixed after updating EmailService
        return ResponseEntity.ok("Not implemented yet");
    }
    */
}