package com.philasande.invoiceflow.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.philasande.invoiceflow.entity.DocumentItem;
import com.philasande.invoiceflow.entity.DocumentSettings;
import com.philasande.invoiceflow.entity.Quotation;
import com.philasande.invoiceflow.entity.User;
import com.philasande.invoiceflow.enums.DocumentStatus;
import com.philasande.invoiceflow.repository.QuotationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuotationService {

private final QuotationRepository quotationRepository;
private final DocumentSettingsService documentSettingsService;
private final DocumentNumberService documentNumberService;

@Transactional
public Quotation createQuotation(Quotation quotation, List<DocumentItem> items, User user){
    DocumentSettings settings = documentSettingsService.getOrCreateSettings(user);
    quotation.setUser(user);
    quotation.setQuotationNumber(documentNumberService.generateQuotationNumber(user));
    quotation.setStatus(DocumentStatus.DRAFT);
    quotation.setIssueDate(LocalDate.now());
    quotation.setDueDate(LocalDate.now().plusDays(settings.getDefaultDueDays()));
    quotation.setTerms(settings.getDefaultTerms());
    quotation.setNotes(settings.getDefaultNotes());

      if (items != null) {
         items.forEach(item -> item.setQuotation(quotation));
         quotation.setItems(items);
      }
         calculateTotals(quotation);
         return quotationRepository.save(quotation);
      }


 @Transactional
 public void updateQuotationStatus(Long id, DocumentStatus newStatus, User user){
    Quotation quotation = findByIdAndUser(id, user);
    quotation.setStatus(newStatus);
    quotationRepository.save(quotation);
 }

 @Transactional
 public void deleteQuotation(Long id, User user){
    Quotation quotation = findByIdAndUser(id, user);
    if (quotation.getStatus() != DocumentStatus.DRAFT) {
        throw new RuntimeException("Only DRAFT quotations can be deleted");  
    }
    quotation.setIsDeleted(true);
    quotationRepository.save(quotation);
 }


public Quotation findByIdAndUser(Long id, User user){
    return quotationRepository.findByIdAndUserAndIsDeletedFalse(id, user).orElseThrow(()-> new RuntimeException("Quotation not found"));
}

public List<Quotation> findAllByUser(User user){
    return quotationRepository.findByUserAndIsDeletedFalse(user);
}

private void calculateTotals(Quotation quotation){
    if (quotation.getItems() == null || quotation.getItems().isEmpty()) {
        quotation.setSubtotal(BigDecimal.ZERO);
        quotation.setTotal(BigDecimal.ZERO);
        return;
         }
        BigDecimal subtotal = quotation.getItems().stream().map(DocumentItem::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        quotation.setSubtotal(subtotal);
        quotation.setTotal(subtotal.add(quotation.getTaxAmount()).subtract(quotation.getDiscount())); 
    }

}
