package com.philasande.invoiceflow.service;

import com.philasande.invoiceflow.dto.InvoiceRequestDto;
import com.philasande.invoiceflow.entity.*;
import com.philasande.invoiceflow.exception.ResourceNotFoundException;
import com.philasande.invoiceflow.repository.CompanyProfileRepository;
import com.philasande.invoiceflow.repository.CustomerRepository;
import com.philasande.invoiceflow.repository.InvoiceRepository;
import com.philasande.invoiceflow.repository.QuotationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final QuotationRepository quotationRepository;
    private final CustomerRepository customerRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final MapperService mapperService;

    public Invoice createInvoice(InvoiceRequestDto dto, User user) {
        Invoice invoice = mapperService.toEntity(dto, user);

        long count = invoiceRepository.count() + 1;
        invoice.setInvoiceNumber(String.format("INV-%04d", count));
        invoice.setStatus(DocumentStatus.DRAFT);

        if (dto.getCustomerId() != null) {
            Customer customer = customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + dto.getCustomerId()));
            invoice.setCustomer(customer);
        }

        if (dto.getCompanyProfileId() != null) {
            CompanyProfile company = companyProfileRepository.findById(dto.getCompanyProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company Profile not found with id: " + dto.getCompanyProfileId()));
            invoice.setCompanyProfile(company);
        }

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            List<DocumentItem> items = mapperService.toDocumentItems(dto.getItems());
            for (DocumentItem item : items) {
                item.setInvoice(invoice);
            }
            invoice.setItems(items);
        }

        calculateTotals(invoice);
        return invoiceRepository.save(invoice);
    }

    public Invoice convertFromQuotation(Long quotationId, User user) {
        Quotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation not found with id: " + quotationId));

        if (!quotation.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to convert this quotation");
        }

        if (quotation.getStatus() == DocumentStatus.CONVERTED) {
            throw new RuntimeException("This quotation has already been converted to an invoice");
        }

        Invoice invoice = new Invoice();
        invoice.setUser(user);
        invoice.setCustomer(quotation.getCustomer());
        invoice.setCompanyProfile(quotation.getCompanyProfile());
        invoice.setIssueDate(LocalDate.now());
        invoice.setDueDate(quotation.getDueDate());
        invoice.setDiscount(quotation.getDiscount() != null ? quotation.getDiscount() : BigDecimal.ZERO);
        invoice.setShipping(BigDecimal.ZERO);
        invoice.setNotes(quotation.getNotes());
        invoice.setTerms(quotation.getTerms());
        invoice.setStatus(DocumentStatus.DRAFT);
        invoice.setQuotation(quotation);

        long count = invoiceRepository.count() + 1;
        invoice.setInvoiceNumber(String.format("INV-%04d", count));

        if (quotation.getItems() != null && !quotation.getItems().isEmpty()) {
            List<DocumentItem> newItems = new ArrayList<>();
            for (DocumentItem item : quotation.getItems()) {
                DocumentItem newItem = new DocumentItem();
                newItem.setDescription(item.getDescription());
                newItem.setQuantity(item.getQuantity());
                newItem.setRate(item.getRate());
                newItem.setAmount(item.getAmount());
                newItem.setInvoice(invoice);
                newItems.add(newItem);
            }
            invoice.setItems(newItems);
        }

        calculateTotals(invoice);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        quotation.setStatus(DocumentStatus.CONVERTED);
        quotationRepository.save(quotation);

        return savedInvoice;
    }

    public Invoice getInvoiceById(Long id, User currentUser) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));

        if (!invoice.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not allowed to access this invoice");
        }

        return invoice;
    }

    public Invoice updateInvoice(Long id, InvoiceRequestDto dto, User currentUser) {
        Invoice existing = getInvoiceById(id, currentUser);

        existing.setIssueDate(dto.getIssueDate());
        existing.setDueDate(dto.getDueDate());
        existing.setDiscount(dto.getDiscount() != null ? dto.getDiscount() : BigDecimal.ZERO);
        existing.setShipping(dto.getShipping() != null ? dto.getShipping() : BigDecimal.ZERO);
        existing.setNotes(dto.getNotes());
        existing.setTerms(dto.getTerms());

        if (dto.getCustomerId() != null) {
            Customer customer = customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
            existing.setCustomer(customer);
        }

        if (dto.getCompanyProfileId() != null) {
            CompanyProfile company = companyProfileRepository.findById(dto.getCompanyProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company Profile not found"));
            existing.setCompanyProfile(company);
        }

        if (dto.getItems() != null) {
            existing.getItems().clear();
            List<DocumentItem> items = mapperService.toDocumentItems(dto.getItems());
            for (DocumentItem item : items) {
                item.setInvoice(existing);
            }
            existing.getItems().addAll(items);
        }

        calculateTotals(existing);
        return invoiceRepository.save(existing);
    }

    public void deleteInvoice(Long id, User currentUser) {
        Invoice invoice = getInvoiceById(id, currentUser);
        invoice.setDeleted(true);
        invoiceRepository.save(invoice);
    }

    private void calculateTotals(Invoice invoice) {
        BigDecimal subtotal = BigDecimal.ZERO;
        if (invoice.getItems() != null) {
            for (DocumentItem item : invoice.getItems()) {
                if (item.getAmount() != null) {
                    subtotal = subtotal.add(item.getAmount());
                }
            }
        }
        invoice.setSubtotal(subtotal);
        invoice.setTaxAmount(BigDecimal.ZERO);

        BigDecimal discount = invoice.getDiscount() != null ? invoice.getDiscount() : BigDecimal.ZERO;
        BigDecimal shipping = invoice.getShipping() != null ? invoice.getShipping() : BigDecimal.ZERO;
        invoice.setTotal(subtotal.subtract(discount).add(shipping));
    }

    public List<Invoice> getAllInvoicesByUser(User user) {
        return invoiceRepository.findByUser(user);
    }
}