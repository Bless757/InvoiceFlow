package com.philasande.invoiceflow.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.philasande.invoiceflow.entity.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;

@Service
public class PdfGeneratorService {

    public byte[] generateInvoicePdf(Invoice invoice) {
        return generateDocument(
                "INVOICE",
                invoice.getInvoiceNumber(),
                invoice.getIssueDate() != null ? invoice.getIssueDate().toString() : "",
                invoice.getDueDate() != null ? invoice.getDueDate().toString() : "",
                invoice.getCompanyProfile(),
                invoice.getCustomer(),
                invoice.getItems(),
                invoice.getSubtotal(),
                invoice.getTaxAmount(),
                invoice.getDiscount(),
                invoice.getShipping(),
                invoice.getTotal(),
                invoice.getNotes(),
                invoice.getTerms()
        );
    }

    public byte[] generateQuotationPdf(Quotation quotation) {
        return generateDocument(
                "QUOTATION",
                quotation.getQuotationNumber(),
                quotation.getIssueDate() != null ? quotation.getIssueDate().toString() : "",
                quotation.getDueDate() != null ? quotation.getDueDate().toString() : "",
                quotation.getCompanyProfile(),
                quotation.getCustomer(),
                quotation.getItems(),
                quotation.getSubtotal(),
                quotation.getTaxAmount(),
                quotation.getDiscount(),
                BigDecimal.ZERO,
                quotation.getTotal(),
                quotation.getNotes(),
                quotation.getTerms()
        );
    }

    private byte[] generateDocument(
            String documentType,
            String documentNumber,
            String issueDate,
            String dueDate,
            CompanyProfile company,
            Customer customer,
            List<DocumentItem> items,
            BigDecimal subtotal,
            BigDecimal tax,
            BigDecimal discount,
            BigDecimal shipping,
            BigDecimal total,
            String notes,
            String terms) {

        try {
            Document document = new Document(PageSize.A4, 40, 40, 40, 50);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(new PageNumberEvent());

            document.open();

            
            PdfPTable topHeader = new PdfPTable(2);
            topHeader.setWidthPercentage(100);
            topHeader.setWidths(new float[]{1.5f, 2f});
            topHeader.setSpacingAfter(15);

           
            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            if (company != null && company.getLogoUrl() != null && !company.getLogoUrl().isBlank()) {
                try {
                    Image logo = Image.getInstance(new URL(company.getLogoUrl()));
                    logo.scaleToFit(120, 60);
                    logoCell.addElement(logo);
                } catch (Exception e) {
                    logoCell.addElement(new Paragraph(
                            company.getCompanyName() != null ? company.getCompanyName() : "Your Company",
                            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new Color(33, 37, 41))
                    ));
                }
            } else {
                String companyName = (company != null && company.getCompanyName() != null)
                        ? company.getCompanyName() : "Your Company";
                logoCell.addElement(new Paragraph(companyName,
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new Color(33, 37, 41))));
            }
            topHeader.addCell(logoCell);

            PdfPCell titleCell = new PdfPCell();
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            Paragraph title = new Paragraph(documentType,
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new Color(33, 37, 41)));
            title.setAlignment(Element.ALIGN_RIGHT);
            titleCell.addElement(title);

            Paragraph number = new Paragraph(documentNumber,
                    FontFactory.getFont(FontFactory.HELVETICA, 12, new Color(108, 117, 125)));
            number.setAlignment(Element.ALIGN_RIGHT);
            titleCell.addElement(number);

            topHeader.addCell(titleCell);
            document.add(topHeader);

            
            PdfPTable parties = new PdfPTable(2);
            parties.setWidthPercentage(100);
            parties.setWidths(new float[]{1f, 1f});
            parties.setSpacingAfter(18);

            
            PdfPCell fromCell = new PdfPCell();
            fromCell.setBorder(Rectangle.NO_BORDER);
            fromCell.setPadding(5);
            fromCell.addElement(new Paragraph("FROM", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, new Color(108, 117, 125))));
            fromCell.addElement(new Paragraph(getCompanyName(company), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
            fromCell.addElement(new Paragraph(getCompanyAddress(company), FontFactory.getFont(FontFactory.HELVETICA, 9)));
            fromCell.addElement(new Paragraph(getCompanyContact(company), FontFactory.getFont(FontFactory.HELVETICA, 9)));
            parties.addCell(fromCell);

            
            PdfPCell billToCell = new PdfPCell();
            billToCell.setBorder(Rectangle.NO_BORDER);
            billToCell.setPadding(5);
            billToCell.addElement(new Paragraph("BILL TO", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, new Color(108, 117, 125))));
            billToCell.addElement(new Paragraph(getCustomerName(customer), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
            billToCell.addElement(new Paragraph(getCustomerAddress(customer), FontFactory.getFont(FontFactory.HELVETICA, 9)));
            billToCell.addElement(new Paragraph(getCustomerContact(customer), FontFactory.getFont(FontFactory.HELVETICA, 9)));
            parties.addCell(billToCell);

            document.add(parties);

            
            PdfPTable dates = new PdfPTable(2);
            dates.setWidthPercentage(100);
            dates.setSpacingAfter(12);
            dates.addCell(createSimpleCell("Issue Date: " + issueDate));
            dates.addCell(createSimpleCell("Due Date: " + dueDate));
            document.add(dates);

            
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{5f, 1.2f, 2f, 2f});
            table.setSpacingBefore(5);
            table.setSpacingAfter(12);

            addHeaderCell(table, "Description");
            addHeaderCell(table, "Qty");
            addHeaderCell(table, "Rate (R)");
            addHeaderCell(table, "Amount (R)");

            if (items != null) {
                for (DocumentItem item : items) {
                    table.addCell(createBodyCell(item.getDescription() != null ? item.getDescription() : "-"));
                    table.addCell(createBodyCell(item.getQuantity() != null ? item.getQuantity().toString() : "0"));
                    table.addCell(createBodyCell(format(item.getRate())));
                    table.addCell(createBodyCell(format(item.getAmount())));
                }
            }
            document.add(table);

            
            PdfPTable totals = new PdfPTable(2);
            totals.setWidthPercentage(42);
            totals.setHorizontalAlignment(Element.ALIGN_RIGHT);

            addTotalRow(totals, "Subtotal", format(subtotal), false);
            addTotalRow(totals, "Tax", format(tax), false);
            addTotalRow(totals, "Discount", format(discount), false);
            if (shipping != null && shipping.compareTo(BigDecimal.ZERO) > 0) {
                addTotalRow(totals, "Shipping", format(shipping), false);
            }
            addTotalRow(totals, "TOTAL", "R " + format(total), true);

            document.add(totals);
            document.add(Chunk.NEWLINE);

            
            if (company != null && company.getBankingDetails() != null && !company.getBankingDetails().isBlank()) {
                document.add(new Paragraph("Banking Details", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
                document.add(new Paragraph(company.getBankingDetails(), FontFactory.getFont(FontFactory.HELVETICA, 9)));
                document.add(Chunk.NEWLINE);
            }

            
            if (notes != null && !notes.isBlank()) {
                document.add(new Paragraph("Notes", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
                document.add(new Paragraph(notes, FontFactory.getFont(FontFactory.HELVETICA, 9)));
                document.add(Chunk.NEWLINE);
            }

            
            if (terms != null && !terms.isBlank()) {
                document.add(new Paragraph("Terms & Conditions", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
                document.add(new Paragraph(terms, FontFactory.getFont(FontFactory.HELVETICA, 9)));
            }

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE)));
        cell.setBackgroundColor(new Color(33, 37, 41));
        cell.setPadding(7);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private PdfPCell createBodyCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, 9)));
        cell.setPadding(6);
        cell.setBorderColor(new Color(222, 226, 230));
        return cell;
    }

    private PdfPCell createSimpleCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, 9)));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(3);
        return cell;
    }

    private void addTotalRow(PdfPTable table, String label, String value, boolean isTotal) {
        Font font = isTotal ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)
                : FontFactory.getFont(FontFactory.HELVETICA, 9);

        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setPadding(4);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPadding(4);

        if (isTotal) {
            labelCell.setBackgroundColor(new Color(248, 249, 250));
            valueCell.setBackgroundColor(new Color(248, 249, 250));
        }

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private String format(BigDecimal amount) {
        if (amount == null) return "0.00";
        return String.format("%,.2f", amount);
    }

    private String getCompanyName(CompanyProfile c) {
        return c != null && c.getCompanyName() != null ? c.getCompanyName() : "Your Company Name";
    }

    private String getCompanyAddress(CompanyProfile c) {
        return c != null && c.getAddress() != null ? c.getAddress() : "";
    }

    private String getCompanyContact(CompanyProfile c) {
        if (c == null) return "";
        StringBuilder sb = new StringBuilder();
        if (c.getEmail() != null) sb.append(c.getEmail()).append("\n");
        if (c.getPhone() != null) sb.append(c.getPhone());
        return sb.toString();
    }

    private String getCustomerName(Customer c) {
        return c != null && c.getName() != null ? c.getName() : "Customer Name";
    }

    private String getCustomerAddress(Customer c) {
        return c != null && c.getAddress() != null ? c.getAddress() : "";
    }

    private String getCustomerContact(Customer c) {
        if (c == null) return "";
        StringBuilder sb = new StringBuilder();
        if (c.getEmail() != null) sb.append(c.getEmail()).append("\n");
        if (c.getPhone() != null) sb.append(c.getPhone());
        return sb.toString();
    }

    private static class PageNumberEvent extends PdfPageEventHelper {
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Phrase footer = new Phrase("Page " + writer.getPageNumber(),
                    FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY));
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 15, 0);
        }
    }
}