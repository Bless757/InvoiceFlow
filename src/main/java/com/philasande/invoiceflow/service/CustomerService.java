package com.philasande.invoiceflow.service;

import com.philasande.invoiceflow.dto.CustomerDto;
import com.philasande.invoiceflow.entity.Customer;
import com.philasande.invoiceflow.entity.User;
import com.philasande.invoiceflow.exception.ResourceNotFoundException;
import com.philasande.invoiceflow.repository.CustomerRepository;
import com.philasande.invoiceflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final MapperService mapperService;

    public Customer createCustomer(CustomerDto dto, User user) {
        User realUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + user.getId()));

        Customer customer = mapperService.toEntity(dto);
        customer.setUser(realUser);
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomersByUser(User user) {
        return customerRepository.findByUser(user);
    }

    public Customer getCustomerById(Long id, User currentUser) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        if (!customer.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not allowed to access this customer");
        }
        return customer;
    }

    public void deleteCustomer(Long id, User currentUser) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        
        if (!customer.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not allowed to delete this customer");
        }

        customerRepository.delete(customer);
    }
}