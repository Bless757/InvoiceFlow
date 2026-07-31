package com.philasande.invoiceflow.repository;

import com.philasande.invoiceflow.entity.Invoice;
import com.philasande.invoiceflow.entity.User;
import com.philasande.invoiceflow.entity.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByUser(User user);
    
    List<Invoice> findByUserAndStatus(User user, DocumentStatus status);
    
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
}
