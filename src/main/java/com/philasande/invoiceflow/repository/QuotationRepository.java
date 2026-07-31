package com.philasande.invoiceflow.repository;

import com.philasande.invoiceflow.entity.Quotation;
import com.philasande.invoiceflow.entity.User;
import com.philasande.invoiceflow.entity.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Long> {

    List<Quotation> findByUser(User user);
    
    List<Quotation> findByUserAndStatus(User user, DocumentStatus status);
    
    Optional<Quotation> findByQuotationNumber(String quotationNumber);
}
