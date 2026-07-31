package com.philasande.invoiceflow.service;

import com.philasande.invoiceflow.dto.*;
import com.philasande.invoiceflow.entity.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MapperService {

    
    public Customer toEntity(CustomerDto dto) {
        Customer customer = new Customer();
        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        customer.setBillingAddress(dto.getBillingAddress());
        customer.setShippingAddress(dto.getShippingAddress());
        customer.setCompanyName(dto.getCompanyName());
        return customer;
    }

    public CustomerDto toDto(Customer customer) {
        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setAddress(customer.getAddress());
        dto.setBillingAddress(customer.getBillingAddress());
        dto.setShippingAddress(customer.getShippingAddress());
        dto.setCompanyName(customer.getCompanyName());
        dto.setCreatedAt(customer.getCreatedAt());
        return dto;
    }

    
    public Quotation toEntity(QuotationRequestDto dto, User user) {
        Quotation quotation = new Quotation();
        quotation.setUser(user);
        quotation.setIssueDate(dto.getIssueDate());
        quotation.setDueDate(dto.getDueDate());
        quotation.setDiscount(dto.getDiscount() != null ? dto.getDiscount() : BigDecimal.ZERO);
        quotation.setNotes(dto.getNotes());
        quotation.setTerms(dto.getTerms());
        return quotation;
    }

    public QuotationResponseDto toQuotationResponseDto(Quotation quotation) {
        QuotationResponseDto dto = new QuotationResponseDto();
        dto.setId(quotation.getId());
        dto.setQuotationNumber(quotation.getQuotationNumber());
        dto.setIssueDate(quotation.getIssueDate());
        dto.setDueDate(quotation.getDueDate());
        dto.setStatus(quotation.getStatus() != null ? quotation.getStatus().name() : null);
        dto.setSubtotal(quotation.getSubtotal());
        dto.setTaxAmount(quotation.getTaxAmount());
        dto.setDiscount(quotation.getDiscount());
        dto.setTotal(quotation.getTotal());
        dto.setNotes(quotation.getNotes());
        dto.setTerms(quotation.getTerms());
        dto.setCreatedAt(quotation.getCreatedAt());
        dto.setItems(toQuotationItemDtos(quotation.getItems()));
        return dto;
    }

    
    public Invoice toEntity(InvoiceRequestDto dto, User user) {
        Invoice invoice = new Invoice();
        invoice.setUser(user);
        invoice.setIssueDate(dto.getIssueDate());
        invoice.setDueDate(dto.getDueDate());
        invoice.setDiscount(dto.getDiscount() != null ? dto.getDiscount() : BigDecimal.ZERO);
        invoice.setShipping(dto.getShipping() != null ? dto.getShipping() : BigDecimal.ZERO);
        invoice.setNotes(dto.getNotes());
        invoice.setTerms(dto.getTerms());
        return invoice;
    }

    public InvoiceResponseDto toInvoiceResponseDto(Invoice invoice) {
        InvoiceResponseDto dto = new InvoiceResponseDto();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setIssueDate(invoice.getIssueDate());
        dto.setDueDate(invoice.getDueDate());
        dto.setStatus(invoice.getStatus() != null ? invoice.getStatus().name() : null);
        dto.setSubtotal(invoice.getSubtotal());
        dto.setTaxAmount(invoice.getTaxAmount());
        dto.setDiscount(invoice.getDiscount());
        dto.setShipping(invoice.getShipping());
        dto.setTotal(invoice.getTotal());
        dto.setNotes(invoice.getNotes());
        dto.setCreatedAt(invoice.getCreatedAt());
        dto.setItems(toQuotationItemDtos(invoice.getItems()));
        return dto;
    }

   
    public DocumentItem toDocumentItem(QuotationItemDto dto) {
    DocumentItem item = new DocumentItem();
    item.setDescription(dto.getDescription());
    item.setQuantity(dto.getQuantity());
    item.setRate(dto.getRate());
    if (dto.getAmount() != null) {
        item.setAmount(dto.getAmount());
    } else if (dto.getRate() != null && dto.getQuantity() != null) {
        item.setAmount(dto.getRate().multiply(BigDecimal.valueOf(dto.getQuantity())));
    }
    item.setNotes(dto.getNotes());
    return item;
}

    public QuotationItemDto toQuotationItemDto(DocumentItem item) {
        QuotationItemDto dto = new QuotationItemDto();
        dto.setDescription(item.getDescription());
        dto.setQuantity(item.getQuantity());
        dto.setRate(item.getRate());
        dto.setAmount(item.getAmount());
        dto.setNotes(item.getNotes());
        return dto;
    }

    public List<DocumentItem> toDocumentItems(List<QuotationItemDto> dtos) {
        if (dtos == null || dtos.isEmpty()) return List.of();
        return dtos.stream().map(this::toDocumentItem).collect(Collectors.toList());
    }

    public List<QuotationItemDto> toQuotationItemDtos(List<DocumentItem> items) {
        if (items == null || items.isEmpty()) return List.of();
        return items.stream().map(this::toQuotationItemDto).collect(Collectors.toList());
    }
}