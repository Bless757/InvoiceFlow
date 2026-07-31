package com.philasande.invoiceflow.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private CompanyProfile companyProfile;

    @Column(name = "invoice_number", unique = true, nullable = false)
    private String invoiceNumber;

    @Column(nullable = false)
    private LocalDate issueDate;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status = DocumentStatus.DRAFT;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DocumentItem> items = new ArrayList<>();

    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal taxAmount = BigDecimal.ZERO;
    private BigDecimal discount = BigDecimal.ZERO;
    private BigDecimal shipping = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;

    @Column(length = 1000)
    private String notes;

    @Column(length = 500)
    private String terms;

    private boolean isDeleted = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    
    @OneToOne
    @JoinColumn(name = "quotation_id")
    private Quotation quotation;

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
