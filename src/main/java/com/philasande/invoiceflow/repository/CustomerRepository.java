package com.philasande.invoiceflow.repository;

import com.philasande.invoiceflow.entity.Customer;
import com.philasande.invoiceflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByUser(User user);
    
    List<Customer> findByUserId(Long userId);
    
    boolean existsByEmailAndUserId(String email, Long userId);
}
