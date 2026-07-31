package com.philasande.invoiceflow.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class QuotationItemDto {

    private String description;
    private Integer quantity;
    private BigDecimal rate;
    private BigDecimal amount;
    private String notes;
}
