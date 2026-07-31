package com.philasande.invoiceflow.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuotationResponseDto {

    private Long id;
    private String quotationNumber;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private String status;
    private List<QuotationItemDto> items;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal discount;
    private BigDecimal total;
    private String notes;
    private String terms;
    private LocalDateTime createdAt;
}
