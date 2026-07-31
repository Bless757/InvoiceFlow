package com.philasande.invoiceflow.service;

import com.philasande.invoiceflow.entity.Invoice;
import com.philasande.invoiceflow.entity.Quotation;
import com.philasande.invoiceflow.entity.User;
import org.springframework.stereotype.Service;

@Service
public class ConversionService {

    public Invoice convertQuotationToInvoice(Quotation quotation, User user) {
        Invoice invoice = new Invoice();
        
        invoice.setUser(user);
        invoice.setCustomer(quotation.getCustomer());
        invoice.setCompanyProfile(quotation.getCompanyProfile());
        invoice.setIssueDate(quotation.getIssueDate());
        invoice.setDueDate(quotation.getDueDate());
        invoice.setItems(quotation.getItems());  // Copy items
        invoice.setSubtotal(quotation.getSubtotal());
        invoice.setTaxAmount(quotation.getTaxAmount());
        invoice.setDiscount(quotation.getDiscount());
        invoice.setTotal(quotation.getTotal());
        invoice.setNotes(quotation.getNotes());
        invoice.setTerms(quotation.getTerms());
        invoice.setQuotation(quotation);   // Link back to quotation

        return invoice;
    }
}
