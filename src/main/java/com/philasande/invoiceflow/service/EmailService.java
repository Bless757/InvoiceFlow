package com.philasande.invoiceflow.service;

import com.philasande.invoiceflow.entity.Invoice;
import com.philasande.invoiceflow.entity.Quotation;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final PdfGeneratorService pdfGeneratorService;

    public void sendInvoiceEmail(Invoice invoice, String toEmail) {
        try {
            byte[] pdfBytes = pdfGeneratorService.generateInvoicePdf(invoice);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Invoice " + invoice.getInvoiceNumber() + " from Philasande Digital Solutions");
            helper.setText(buildInvoiceEmailBody(invoice), true);

            helper.addAttachment(invoice.getInvoiceNumber() + ".pdf", new ByteArrayResource(pdfBytes));

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send invoice email", e);
        }
    }

    public void sendQuotationEmail(Quotation quotation, String toEmail) {
        try {
            byte[] pdfBytes = pdfGeneratorService.generateQuotationPdf(quotation);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Quotation " + quotation.getQuotationNumber() + " from Philasande Digital Solutions");
            helper.setText(buildQuotationEmailBody(quotation), true);

            helper.addAttachment(quotation.getQuotationNumber() + ".pdf", new ByteArrayResource(pdfBytes));

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send quotation email", e);
        }
    }

    private String buildInvoiceEmailBody(Invoice invoice) {
        return """
                <h2>Invoice %s</h2>
                <p>Dear Customer,</p>
                <p>Please find attached your invoice <strong>%s</strong>.</p>
                <p><strong>Total Amount:</strong> R %s</p>
                <p>Thank you for your business.</p>
                <br>
                <p>Best regards,<br>Philasande Digital Solutions</p>
                """.formatted(
                invoice.getInvoiceNumber(),
                invoice.getInvoiceNumber(),
                invoice.getTotal()
        );
    }

    private String buildQuotationEmailBody(Quotation quotation) {
        return """
                <h2>Quotation %s</h2>
                <p>Dear Customer,</p>
                <p>Please find attached your quotation <strong>%s</strong>.</p>
                <p><strong>Total Amount:</strong> R %s</p>
                <p>We look forward to working with you.</p>
                <br>
                <p>Best regards,<br>Philasande Digital Solutions</p>
                """.formatted(
                quotation.getQuotationNumber(),
                quotation.getQuotationNumber(),
                quotation.getTotal()
        );
    }
}