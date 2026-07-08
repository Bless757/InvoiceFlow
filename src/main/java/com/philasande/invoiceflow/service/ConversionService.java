package com.philasande.invoiceflow.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.philasande.invoiceflow.entity.DocumentItem;
import com.philasande.invoiceflow.entity.Invoice;
import com.philasande.invoiceflow.entity.Quotation;
import com.philasande.invoiceflow.entity.User;
import com.philasande.invoiceflow.enums.DocumentStatus;
import com.philasande.invoiceflow.repository.QuotationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConversionService {

    private final QuotationRepository quotationRepository;
    private final InvoiceService invoiceService;

  @Transactional
  public Invoice convertQuotationToInvoice(Long quotationId, User user){
    Quotation quotation = quotationRepository.findByIdAndUserAndIsDeletedFalse(quotationId, user).orElseThrow(()-> new RuntimeException("Quotation not found"));

    if (quotation.getStatus() != DocumentStatus.ACCEPTED){
        throw new RuntimeException("Only ACCEPTED quotations can be converted");
    }

    Invoice invoice = new Invoice();
    invoice.setUser(user);

    invoice.setCustomer(quotation.getCustomer());
    invoice.setCompanyProfile(quotation.getCompanyProfile());
    invoice.setQuotation(quotation);

    invoice.setIssueDate(quotation.getIssueDate());
    invoice.setDueDate(quotation.getDueDate());
    invoice.setNotes(quotation.getNotes());
    invoice.setTerms(quotation.getTerms());
    invoice.setSubtotal(quotation.getSubtotal());
    invoice.setTaxAmount(quotation.getTaxAmount());
    invoice.setDiscount(quotation.getDiscount());
    invoice.setTotal(quotation.getTotal());
    invoice.setAmountPaid(BigDecimal.ZERO);

    List<DocumentItem> newItems = new ArrayList<>();
    for (DocumentItem original : quotation.getItems()){
        DocumentItem newItem = new DocumentItem();

        newItem.setDescription(original.getDescription());
        newItem.setQuantity(original.getQuantity());
        newItem.setUnitPrice(original.getUnitPrice());
        newItem.setTaxRate(original.getTaxRate());
        newItem.setAmount(original.getAmount());
        newItems.add(newItem);
    }

    Invoice savedInvoice = invoiceService.createInvoice(invoice, newItems, user);

    quotation.setStatus(DocumentStatus.CONVERTED);
    quotationRepository.save(quotation);
    return savedInvoice;
  }
}
