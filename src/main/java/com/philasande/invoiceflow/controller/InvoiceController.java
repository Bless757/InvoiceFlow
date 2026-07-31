package com.philasande.invoiceflow.controller;

import com.philasande.invoiceflow.dto.InvoiceRequestDto;
import com.philasande.invoiceflow.dto.InvoiceResponseDto;
import com.philasande.invoiceflow.entity.Invoice;
import com.philasande.invoiceflow.entity.User;
import com.philasande.invoiceflow.security.SecurityUtils;
import com.philasande.invoiceflow.service.EmailService;
import com.philasande.invoiceflow.service.InvoiceService;
import com.philasande.invoiceflow.service.MapperService;
import com.philasande.invoiceflow.service.PdfGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final MapperService mapperService;
    private final PdfGeneratorService pdfGeneratorService;
    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<InvoiceResponseDto> createInvoice(@RequestBody InvoiceRequestDto dto) {
        User currentUser = SecurityUtils.getCurrentUser();
        Invoice saved = invoiceService.createInvoice(dto, currentUser);
        return ResponseEntity.ok(mapperService.toInvoiceResponseDto(saved));
    }

    @GetMapping
    public ResponseEntity<List<InvoiceResponseDto>> getMyInvoices() {
        User currentUser = SecurityUtils.getCurrentUser();
        List<Invoice> invoices = invoiceService.getAllInvoicesByUser(currentUser);
        List<InvoiceResponseDto> responses = invoices.stream()
                .map(mapperService::toInvoiceResponseDto)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponseDto> getInvoice(@PathVariable Long id) {
        User currentUser = SecurityUtils.getCurrentUser();
        Invoice invoice = invoiceService.getInvoiceById(id, currentUser);
        return ResponseEntity.ok(mapperService.toInvoiceResponseDto(invoice));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceResponseDto> updateInvoice(
            @PathVariable Long id,
            @RequestBody InvoiceRequestDto dto) {
        User currentUser = SecurityUtils.getCurrentUser();
        Invoice updated = invoiceService.updateInvoice(id, dto, currentUser);
        return ResponseEntity.ok(mapperService.toInvoiceResponseDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInvoice(@PathVariable Long id) {
        User currentUser = SecurityUtils.getCurrentUser();
        invoiceService.deleteInvoice(id, currentUser);
        return ResponseEntity.ok("Invoice deleted successfully");
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable Long id) {
        User currentUser = SecurityUtils.getCurrentUser();
        Invoice invoice = invoiceService.getInvoiceById(id, currentUser);
        byte[] pdfBytes = pdfGeneratorService.generateInvoicePdf(invoice);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + invoice.getInvoiceNumber() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<String> sendInvoice(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        String toEmail = request.get("email");
        if (toEmail == null || toEmail.isBlank()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        User currentUser = SecurityUtils.getCurrentUser();
        Invoice invoice = invoiceService.getInvoiceById(id, currentUser);
        emailService.sendInvoiceEmail(invoice, toEmail);

        return ResponseEntity.ok("Invoice sent successfully to " + toEmail);
    }
}