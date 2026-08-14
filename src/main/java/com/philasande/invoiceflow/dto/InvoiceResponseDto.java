package com.philasande.invoiceflow.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InvoiceResponseDto {
    private Long id;
    private String invoiceNumber;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private String status;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal discount;
    private BigDecimal shipping;
    private BigDecimal total;
    private String notes;
    private String terms;
    private LocalDateTime createdAt;

   
    private Long customerId;
    private String customerName;

    private List<QuotationItemDto> items;
}
