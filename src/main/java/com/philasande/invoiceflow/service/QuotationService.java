package com.philasande.invoiceflow.service;

import com.philasande.invoiceflow.dto.QuotationRequestDto;
import com.philasande.invoiceflow.entity.*;
import com.philasande.invoiceflow.exception.ResourceNotFoundException;
import com.philasande.invoiceflow.repository.CompanyProfileRepository;
import com.philasande.invoiceflow.repository.CustomerRepository;
import com.philasande.invoiceflow.repository.QuotationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuotationService {

    private final QuotationRepository quotationRepository;
    private final CustomerRepository customerRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final MapperService mapperService;

    public Quotation createQuotation(QuotationRequestDto dto, User user) {
        Quotation quotation = mapperService.toEntity(dto, user);

        long count = quotationRepository.count() + 1;
        quotation.setQuotationNumber(String.format("QUO-%04d", count));
        quotation.setStatus(DocumentStatus.DRAFT);

        if (dto.getCustomerId() != null) {
            Customer customer = customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + dto.getCustomerId()));
            quotation.setCustomer(customer);
        }

        if (dto.getCompanyProfileId() != null) {
            CompanyProfile company = companyProfileRepository.findById(dto.getCompanyProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company Profile not found with id: " + dto.getCompanyProfileId()));
            quotation.setCompanyProfile(company);
        }

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            List<DocumentItem> items = mapperService.toDocumentItems(dto.getItems());
            for (DocumentItem item : items) {
                item.setQuotation(quotation);
            }
            quotation.setItems(items);
        }

        calculateTotals(quotation);
        return quotationRepository.save(quotation);
    }

    public Quotation getQuotationById(Long id, User currentUser) {
        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation not found with id: " + id));

        if (!quotation.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not allowed to access this quotation");
        }

        return quotation;
    }

    public Quotation updateQuotation(Long id, QuotationRequestDto dto, User currentUser) {
        Quotation existing = getQuotationById(id, currentUser);

        existing.setIssueDate(dto.getIssueDate());
        existing.setDueDate(dto.getDueDate());
        existing.setDiscount(dto.getDiscount() != null ? dto.getDiscount() : BigDecimal.ZERO);
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
                item.setQuotation(existing);
            }
            existing.getItems().addAll(items);
        }

        calculateTotals(existing);
        return quotationRepository.save(existing);
    }

    public void deleteQuotation(Long id, User currentUser) {
        Quotation quotation = getQuotationById(id, currentUser);
        quotation.setDeleted(true);
        quotationRepository.save(quotation);
    }

    private void calculateTotals(Quotation quotation) {
        BigDecimal subtotal = BigDecimal.ZERO;
        if (quotation.getItems() != null) {
            for (DocumentItem item : quotation.getItems()) {
                if (item.getAmount() != null) {
                    subtotal = subtotal.add(item.getAmount());
                }
            }
        }
        quotation.setSubtotal(subtotal);
        quotation.setTaxAmount(BigDecimal.ZERO);
        BigDecimal discount = quotation.getDiscount() != null ? quotation.getDiscount() : BigDecimal.ZERO;
        quotation.setTotal(subtotal.subtract(discount));
    }

    public List<Quotation> getAllQuotationsByUser(User user) {
        return quotationRepository.findByUser(user);
    }
}