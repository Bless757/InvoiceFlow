package com.philasande.invoiceflow.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DocumentSettingsDto {

    private String nextQuotationNumber;
    private String nextInvoiceNumber;
    private String defaultCurrency;
    private BigDecimal defaultTaxRate;
    private Integer defaultDueDays;
    private String defaultTerms;
    private String defaultNotes;
}
