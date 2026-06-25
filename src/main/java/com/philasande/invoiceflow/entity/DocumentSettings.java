package com.philasande.invoiceflow.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "document_settings")
public class DocumentSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "next_quotation_number")
    private String nextQuotationNumber = "QUO-0001";

    @Column(name = "next_invoice_number")
    private String nextInvoiceNumber = "INV-0001";

    @Column(name = "default_currency", length = 3)
    private String defaultCurrency = "ZAR";

    @Column(name = "default_tax_rate")
    private BigDecimal defaultTaxRate = BigDecimal.valueOf(15.0);

    private Integer defaultDueDays = 30;

    @Column(length = 500)
    private String defaultTerms;

    @Column(length = 1000)
    private String defaultNotes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
