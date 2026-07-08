package com.philasande.invoiceflow.service;

import com.philasande.invoiceflow.entity.Customer;
import com.philasande.invoiceflow.entity.User;
import com.philasande.invoiceflow.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomersByUser(User user) {
        return customerRepository.findByUser(user);
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }
}
