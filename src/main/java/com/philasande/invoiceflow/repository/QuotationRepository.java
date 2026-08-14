package com.philasande.invoiceflow.repository;

import com.philasande.invoiceflow.entity.Quotation;
import com.philasande.invoiceflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Long> {

    List<Quotation> findByUser(User user);

    long countByUser(User user);
}
