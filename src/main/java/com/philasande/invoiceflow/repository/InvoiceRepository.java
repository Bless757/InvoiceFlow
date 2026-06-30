package com.philasande.invoiceflow.repository;

import com.philasande.invoiceflow.entity.Invoice;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice,Long> {

   // find invoice by user
   Page<Invoice> findByUserAndIsDeletedFalse(User user, Pageable pageable);

   // find invoice by user and status
   Page<Invoice> findByUserAndStatusAndIsDeletedFalse(User user, DocumentStatus status, Pageable pageable);

   // find single invoice by ID and user
   Optional<Invoice> findByIdAndUserAndIsDeletedFalse(Long id, User user);

   // find invoice by invoice number
   Optional<Invoice> findByInvoiceNumberAndIsDeletedFalse(String invoiceNumber);

   // check if invoice number exists
   boolean existsByInvoiceNumberAndIsDeletedFalse(String invoiceNumber);

   // find invoices that are overdue
    @Query("SELECT i FROM Invoice i WHERE i.dueDate < :today " + "AND i.status != :cancelledStatus " + "AND i.isDeleted = false ")
    List<Invoice> findOverdueInvoices(@Param("today")LocalDate today, @Param("cancelledStatus")DocumentStatus cancelledStatus);

   // find invoice by quotation
   Optional<Invoice> findByQuotationAndIsDeletedFalse(Quotation quotation);

   // update invoice status
   @Modifying
   @Transactional
   @Query("UPDATE Invoice i SET i.status = :newStatus WHERE i.id = :invoiceId")
   void updateStatus(@Param("invoiceId") Long invoiceId, @Param("newStatus") DocumentStatus newStatus);

   // update amount paid
   @Modifying
   @Transactional
   @Query("UPDATE Invoice i SET i.amountPaid = :amountPaid WHERE i.id = :invoiceId")
   void updateAmountPaid(@Param("invoiceId") Long invoiceId, @Param("amountPaid")BigDecimal amountPaid);

  // count invoices by status
  long countByUserAndStatusAndIsDeletedFalse(User user, DocumentStatus status);

  // count total invoice
  long countByUserAndIsDeletedFalse(User user);

  // total/sum of all paid invoices
  @Query("SELECT COALESCE(SUM(i.total), 0) FROM Invoice i " +
          "WHERE i.user = :user AND i.status = :paidStatus AND i.isDeleted = false")
  BigDecimal sumTotalByUserAndStatus(@Param("user") User user, @Param("paidStatus") DocumentStatus paidStatus);

  // search invoice by number or customer name
  @Query("SELECT i FROM Invoice i WHERE i.user = :user AND i.isDeleted = false " +
          "AND (LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
          "OR LOWER(i.customer.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
  Page<Invoice> searchByUserAndSearchTerm(
          @Param("user") User user,
          @Param("searchTerm") String searchTerm,
          Pageable pageable
  );
}
