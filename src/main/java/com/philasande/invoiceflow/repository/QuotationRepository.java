package com.philasande.invoiceflow.repository;

import com.philasande.invoiceflow.entity.Quotation;
import com.philasande.invoiceflow.entity.User;
import com.philasande.invoiceflow.enums.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Long> {

    //    find quotes by user
    Page<Quotation> findByUserAndIsDeletedFalse(User user, Pageable pageable);

    //    find quotes by user and status
    Page<Quotation> findByUserAndStatusAndIsDeletedFalse(User user, DocumentStatus status, Pageable pageable);

    // find single quote by ID and user
    Optional<Quotation> findByIdAndUserAndIsDeletedFalse(Long id, User user);

    // find quote by quotation number
    Optional<Quotation> findByQuotationNumberAndIsDeletedFalse(String quotationNumber);

    // check if quotation number exist
    boolean existsByQuotationNumberAndIsDeletedFalse(String quotationNumber);

    // find quotations that are approved
    List<Quotation> findByUserAndStatusAndIsDeletedFalse(User user, DocumentStatus status);

    // find quotations that are expired
    @Query("SELECT q FROM Quotation q WHERE q.dueDate < :today AND q.status != :expiredStatus AND q.isDeleted = false")
    List<Quotation> findByExpiredQuotations(@Param("today")LocalDate today, @Param("expiredStatus")DocumentStatus expiredStatus);

    // update quotation status
    @Modifying
    @Transactional
    @Query("UPDATE Quotation q SET q.status = :newStatus WHERE q.id = :quotationId")
    void updateStatus(
            @Param("quotationId") Long quotationId,
            @Param("newStatus") DocumentStatus newStatus
    );


    // count total quotation for status
    long countByUserAndStatusAndIsDeletedFalse(User user, DocumentStatus status);

    // count total quotation for user
    long countByUserAndIsDeletedFalse(User user);

    // search quotation by number or customer name
    @Query("SELECT q FROM Quotation q WHERE q.user = :user AND q.isDeleted = false " +
            "AND (LOWER(q.quotationNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(q.customer.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Quotation> searchByUserAndSearchTerm(
            @Param("user") User user,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );

}
