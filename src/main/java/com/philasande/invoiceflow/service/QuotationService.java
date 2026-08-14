package com.philasande.invoiceflow.service;

import com.philasande.invoiceflow.dto.QuotationRequestDto;
import com.philasande.invoiceflow.dto.QuotationResponseDto;
import com.philasande.invoiceflow.entity.*;
import com.philasande.invoiceflow.exception.ResourceNotFoundException;
import com.philasande.invoiceflow.repository.CustomerRepository;
import com.philasande.invoiceflow.repository.QuotationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuotationService {

    private final QuotationRepository quotationRepository;
    private final CustomerRepository customerRepository;
    private final MapperService mapperService;

    @Transactional
    public QuotationResponseDto createQuotation(QuotationRequestDto dto, User currentUser) {
       
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + dto.getCustomerId()));

        if (!customer.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not allowed to use this customer");
        }

        
        Quotation quotation = mapperService.toEntity(dto, currentUser);
        quotation.setCustomer(customer);
        quotation.setStatus(DocumentStatus.DRAFT);

        
        quotation.setQuotationNumber(generateNextQuotationNumber(currentUser));

        
        List<DocumentItem> items = mapperService.toDocumentItems(dto.getItems());
        items.forEach(item -> item.setQuotation(quotation));
        quotation.setItems(items);

       
        calculateTotals(quotation);

        
        Quotation saved = quotationRepository.save(quotation);

        return mapperService.toQuotationResponseDto(saved);
    }

    public List<QuotationResponseDto> getAllQuotations(User currentUser) {
        return quotationRepository.findByUser(currentUser).stream()
                .map(mapperService::toQuotationResponseDto)
                .collect(Collectors.toList());
    }

    public QuotationResponseDto getQuotationById(Long id, User currentUser) {
        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation not found with id: " + id));

        if (!quotation.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not allowed to access this quotation");
        }

        return mapperService.toQuotationResponseDto(quotation);
    }

    @Transactional
    public void deleteQuotation(Long id, User currentUser) {
        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation not found with id: " + id));

        if (!quotation.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not allowed to delete this quotation");
        }

        quotationRepository.delete(quotation);
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

        BigDecimal discount = quotation.getDiscount() != null ? quotation.getDiscount() : BigDecimal.ZERO;
        BigDecimal taxAmount = quotation.getTaxAmount() != null ? quotation.getTaxAmount() : BigDecimal.ZERO;

        BigDecimal total = subtotal.subtract(discount).add(taxAmount);
        quotation.setTotal(total);
    }

    private String generateNextQuotationNumber(User user) {
        List<Quotation> existing = quotationRepository.findByUser(user);

        int max = 0;
        for (Quotation q : existing) {
            if (q.getQuotationNumber() != null && q.getQuotationNumber().startsWith("QUO-")) {
                try {
                    int num = Integer.parseInt(q.getQuotationNumber().substring(4));
                    if (num > max) {
                        max = num;
                    }
                } catch (NumberFormatException ignored) {
                    
                }
            }
        }

        return String.format("QUO-%04d", max + 1);
    }
}