package com.philasande.invoiceflow.service;

import org.springframework.stereotype.Service;
import com.philasande.invoiceflow.entity.DocumentSettings;
import com.philasande.invoiceflow.entity.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentNumberService {

    private final DocumentSettingsService documentSettingsService;
    
    public String generateQuotationNumber(User user){
      DocumentSettings settings = documentSettingsService.getOrCreateSettings(user);
      String current = settings.getNextQuotationNumber();
      settings.setNextQuotationNumber(incrementNumber(current));
      documentSettingsService.updateSettings(settings);
      return current;
    }
    
    public String generateInvoiceNumber(User user){
        DocumentSettings settings = documentSettingsService.getOrCreateSettings(user);
        String current = settings.getNextInvoiceNumber();
        settings.setNextInvoiceNumber(incrementNumber(current));
        documentSettingsService.updateSettings(settings);
        return current;
    }

    private String incrementNumber(String number){
        String prefix = number.substring(0, number.lastIndexOf("-") + 1);
        String numPart = number.substring(number.lastIndexOf("-") + 1);
        int num = Integer.parseInt(numPart) + 1;
        return prefix + String.format("%04d", num);
    }

}
