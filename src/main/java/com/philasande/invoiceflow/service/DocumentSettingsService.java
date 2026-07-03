package com.philasande.invoiceflow.service;

import com.philasande.invoiceflow.entity.DocumentSettings;
import com.philasande.invoiceflow.entity.User;
import com.philasande.invoiceflow.repository.DocumentSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentSettingsService {

    private final DocumentSettingsRepository settingsRepository;

    public DocumentSettings getOrCreateSettings(User user) {
        return settingsRepository.findByUser(user)
                .orElseGet(() -> {
                    DocumentSettings settings = new DocumentSettings();
                    settings.setUser(user);
                    return settingsRepository.save(settings);
                });
    }

    public DocumentSettings updateSettings(DocumentSettings settings) {
        return settingsRepository.save(settings);
    }
}
