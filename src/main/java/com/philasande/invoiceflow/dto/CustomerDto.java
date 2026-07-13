package com.philasande.invoiceflow.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CustomerDto {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String billingAddress;
    private String shippingAddress;
    private String companyName;
    private LocalDateTime createdAt;
}
