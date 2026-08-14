package com.philasande.invoiceflow.controller;

import com.philasande.invoiceflow.dto.CustomerDto;
import com.philasande.invoiceflow.entity.Customer;
import com.philasande.invoiceflow.entity.User;
import com.philasande.invoiceflow.security.SecurityUtils;
import com.philasande.invoiceflow.service.CustomerService;
import com.philasande.invoiceflow.service.MapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final MapperService mapperService;

    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CustomerDto dto) {
        User currentUser = SecurityUtils.getCurrentUser();
        Customer saved = customerService.createCustomer(dto, currentUser);
        return ResponseEntity.ok(mapperService.toDto(saved));
    }

    @GetMapping
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        User currentUser = SecurityUtils.getCurrentUser();
        List<Customer> customers = customerService.getAllCustomersByUser(currentUser);
        List<CustomerDto> response = customers.stream()
                .map(mapperService::toDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomer(@PathVariable Long id) {
        User currentUser = SecurityUtils.getCurrentUser();
        Customer customer = customerService.getCustomerById(id, currentUser);
        return ResponseEntity.ok(mapperService.toDto(customer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        User currentUser = SecurityUtils.getCurrentUser();
        customerService.deleteCustomer(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}