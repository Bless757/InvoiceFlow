package com.philasande.invoiceflow.repository;

import com.philasande.invoiceflow.entity.DocumentItem;
import com.philasande.invoiceflow.entity.Invoice;
import com.philasande.invoiceflow.entity.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentItemRepository extends JpaRepository<DocumentItem, Long> {

  
  List<DocumentItem> findByQuotationAndIsDeletedFalse(Quotation quotation);

  
  List<DocumentItem> findByInvoiceAndIsDeletedFalse(Invoice invoice);

  
  void  deleteByQuotation(Quotation quotation);

  
  void  deleteByInvoice(Invoice invoice);
}
