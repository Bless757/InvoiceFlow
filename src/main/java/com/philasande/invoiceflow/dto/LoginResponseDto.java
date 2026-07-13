package com.philasande.invoiceflow.dto;

import lombok.Data;

@Data
public class LoginResponseDto {

    private Long userId;
    private String fullName;
    private String email;
    private String token;           // Will be used later with JWT
    private String message;
}
