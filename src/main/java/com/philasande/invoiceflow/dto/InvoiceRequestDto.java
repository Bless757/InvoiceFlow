package com.philasande.invoiceflow.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class InvoiceRequestDto {

    private Long customerId;
    private Long companyProfileId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private List<QuotationItemDto> items;
    private BigDecimal discount;
    private BigDecimal shipping;
    private String notes;
    private String terms;
}
