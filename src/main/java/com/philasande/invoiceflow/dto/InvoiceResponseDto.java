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
    private List<QuotationItemDto> items;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal discount;
    private BigDecimal shipping;
    private BigDecimal total;
    private String notes;
    private LocalDateTime createdAt;
}
