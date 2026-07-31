package com.philasande.invoiceflow.controller;

import com.philasande.invoiceflow.dto.DocumentSettingsDto;
import com.philasande.invoiceflow.entity.User;
import com.philasande.invoiceflow.service.DocumentSettingsService;
import com.philasande.invoiceflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final DocumentSettingsService documentSettingsService;

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
       
        return ResponseEntity.ok(new User()); // Will be improved later
    }

    @GetMapping("/settings")
    public ResponseEntity<DocumentSettingsDto> getUserSettings() {
        
        DocumentSettingsDto dto = new DocumentSettingsDto();
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/settings")
    public ResponseEntity<DocumentSettingsDto> updateSettings(@RequestBody DocumentSettingsDto dto) {
        
        return ResponseEntity.ok(dto);
    }
}
