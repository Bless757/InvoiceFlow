package com.philasande.invoiceflow.repository;

import com.philasande.invoiceflow.entity.CompanyProfile;
import com.philasande.invoiceflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Long> {

    Optional<CompanyProfile> findByUser(User user);
    
    Optional<CompanyProfile> findByUserId(Long userId);
}
