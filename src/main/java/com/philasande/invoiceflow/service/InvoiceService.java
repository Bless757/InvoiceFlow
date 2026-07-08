package com.philasande.invoiceflow.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.philasande.invoiceflow.entity.DocumentItem;
import com.philasande.invoiceflow.entity.DocumentSettings;
import com.philasande.invoiceflow.entity.Invoice;
import com.philasande.invoiceflow.entity.User;
import com.philasande.invoiceflow.enums.DocumentStatus;
import com.philasande.invoiceflow.repository.InvoiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final DocumentSettingsService documentSettingsService;
    private final DocumentNumberService documentNumberService;

    @Transactional
    public Invoice createInvoice(Invoice invoice, List<DocumentItem> items, User user){
        DocumentSettings settings = documentSettingsService.getOrCreateSettings(user);
        invoice.setUser(user);
        invoice.setInvoiceNumber(documentNumberService.generateInvoiceNumber(user));

        invoice.setStatus(DocumentStatus.DRAFT);
        invoice.setIssueDate(LocalDate.now());

        invoice.setDueDate(LocalDate.now().plusDays(settings.getDefaultDueDays()));

        invoice.setPaymentTerms(settings.getDefaultTerms());

        invoice.setTerms(settings.getDefaultTerms());
        invoice.setNotes(settings.getDefaultNotes());
        invoice.setAmountPaid(BigDecimal.ZERO);

        if (items != null) {
            items.forEach(item -> item.setInvoice(invoice));
            invoice.setItems(items);
        }

        calculateTotals(invoice);
        return invoiceRepository.save(invoice);
    }


    @Transactional
    public void recordPayment(Long id, BigDecimal amount, User user){
        Invoice invoice = findByIdAndUser(id, user);
        BigDecimal newPaid = invoice.getAmountPaid().add(amount);
        invoice.setAmountPaid(newPaid);

        if (newPaid.compareTo(invoice.getTotal()) >= 0) {
            invoice.setStatus(DocumentStatus.PAID);
        }
        invoiceRepository.save(invoice);
    }



     @Transactional
     public void updateInvoiceStatus(Long id, DocumentStatus newStatus, User user){
        Invoice invoice = findByIdAndUser(id, user);
        invoice.setStatus(newStatus);
        invoiceRepository.save(invoice);
     }

    @Transactional
    public void deleteInvoice(Long id, User user){
        Invoice invoice = findByIdAndUser(id, user);

        if (invoice.getStatus() != DocumentStatus.DRAFT) {
          throw new RuntimeException("Only DRAFT invoices can be deleted");  
        }
        invoice.setIsDeleted(true);
        invoiceRepository.save(invoice);
    } 

   
    public Invoice findByIdAndUser(Long id, User user){
        return invoiceRepository.findByIdAndUserAndIsDeletedFalse(id, user).orElseThrow(()-> new RuntimeException("Invoice not found"));
    }

    public List<Invoice> findAllByUser(User user){
        return invoiceRepository.findByUserAndIsDeletedFalse(user);
    }

   
    private void calculateTotals(Invoice invoice){
        if (invoice.getItems() == null || invoice.getItems().isEmpty()){
            invoice.setSubtotal(BigDecimal.ZERO);
            invoice.setTotal(BigDecimal.ZERO);
            return;
        }
        BigDecimal subtotal = invoice.getItems().stream().map(DocumentItem::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        invoice.setSubtotal(subtotal);

        invoice.setTotal(subtotal.add(invoice.getTaxAmount()).subtract(invoice.getDiscount()).add(invoice.getShipping()));
    }
}
