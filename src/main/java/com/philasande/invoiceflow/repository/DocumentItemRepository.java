package com.philasande.invoiceflow.repository;

import com.philasande.invoiceflow.entity.DocumentItem;
import com.philasande.invoiceflow.entity.Invoice;
import com.philasande.invoiceflow.entity.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentItemRepository extends JpaRepository<DocumentItem, Long> {

  // find items by quotation
  List<DocumentItem> findByQuotationAndIsDeletedFalse(Quotation quotation);

  // find items by invoice
  List<DocumentItem> findByInvoiceAndIsDeletedFalse(Invoice invoice);

  // delete all items for a quotation
  void  deleteByQuotation(Quotation quotation);

  // delete delete all items for an invoice
  void  deleteByInvoice(Invoice invoice);
}
