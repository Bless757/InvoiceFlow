package com.philasande.invoiceflow.controller;

import com.philasande.invoiceflow.dto.InvoiceResponseDto;
import com.philasande.invoiceflow.dto.QuotationRequestDto;
import com.philasande.invoiceflow.dto.QuotationResponseDto;
import com.philasande.invoiceflow.entity.Invoice;
import com.philasande.invoiceflow.entity.Quotation;
import com.philasande.invoiceflow.entity.User;
import com.philasande.invoiceflow.security.SecurityUtils;
import com.philasande.invoiceflow.service.EmailService;
import com.philasande.invoiceflow.service.InvoiceService;
import com.philasande.invoiceflow.service.MapperService;
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
    private final InvoiceService invoiceService;
    private final MapperService mapperService;
    private final PdfGeneratorService pdfGeneratorService;
    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<QuotationResponseDto> createQuotation(@RequestBody QuotationRequestDto dto) {
        User currentUser = SecurityUtils.getCurrentUser();
        Quotation saved = quotationService.createQuotation(dto, currentUser);
        return ResponseEntity.ok(mapperService.toQuotationResponseDto(saved));
    }

    @GetMapping
    public ResponseEntity<List<QuotationResponseDto>> getMyQuotations() {
        User currentUser = SecurityUtils.getCurrentUser();
        List<Quotation> quotations = quotationService.getAllQuotationsByUser(currentUser);
        List<QuotationResponseDto> responses = quotations.stream()
                .map(mapperService::toQuotationResponseDto)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuotationResponseDto> getQuotation(@PathVariable Long id) {
        User currentUser = SecurityUtils.getCurrentUser();
        Quotation quotation = quotationService.getQuotationById(id, currentUser);
        return ResponseEntity.ok(mapperService.toQuotationResponseDto(quotation));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuotationResponseDto> updateQuotation(
            @PathVariable Long id,
            @RequestBody QuotationRequestDto dto) {
        User currentUser = SecurityUtils.getCurrentUser();
        Quotation updated = quotationService.updateQuotation(id, dto, currentUser);
        return ResponseEntity.ok(mapperService.toQuotationResponseDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuotation(@PathVariable Long id) {
        User currentUser = SecurityUtils.getCurrentUser();
        quotationService.deleteQuotation(id, currentUser);
        return ResponseEntity.ok("Quotation deleted successfully");
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadQuotationPdf(@PathVariable Long id) {
        User currentUser = SecurityUtils.getCurrentUser();
        Quotation quotation = quotationService.getQuotationById(id, currentUser);
        byte[] pdfBytes = pdfGeneratorService.generateQuotationPdf(quotation);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + quotation.getQuotationNumber() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<String> sendQuotation(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        String toEmail = request.get("email");
        if (toEmail == null || toEmail.isBlank()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        User currentUser = SecurityUtils.getCurrentUser();
        Quotation quotation = quotationService.getQuotationById(id, currentUser);
        emailService.sendQuotationEmail(quotation, toEmail);

        return ResponseEntity.ok("Quotation sent successfully to " + toEmail);
    }

    @PostMapping("/{id}/convert")
    public ResponseEntity<InvoiceResponseDto> convertToInvoice(@PathVariable Long id) {
        User currentUser = SecurityUtils.getCurrentUser();
        Invoice invoice = invoiceService.convertFromQuotation(id, currentUser);
        return ResponseEntity.ok(mapperService.toInvoiceResponseDto(invoice));
    }
}