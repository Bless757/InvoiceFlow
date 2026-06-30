package com.philasande.invoiceflow.repository;

import com.philasande.invoiceflow.entity.DocumentSettings;
import com.philasande.invoiceflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DocumentSettingsRepository extends JpaRepository<DocumentSettings, Long> {

    Optional<DocumentSettings> findByUser(User user);
    
    Optional<DocumentSettings> findByUserId(Long userId);
}
