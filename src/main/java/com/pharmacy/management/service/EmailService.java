package com.pharmacy.management.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.pharmacy.management.model.Medicine;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendBillEmail(String toEmail, Medicine medicine, int quantity, double totalPrice)
            throws MessagingException, IOException {
        // Generate PDF
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Pharmacy Management System - Bill Invoice"));
        document.add(new Paragraph("------------------------------------------------"));
        document.add(new Paragraph("Medicine Name: " + medicine.getName()));
        document.add(new Paragraph("Description: " + medicine.getDescription()));
        document.add(new Paragraph("Price per Unit: ₹" + medicine.getPrice()));
        document.add(new Paragraph("Quantity: " + quantity));
        document.add(new Paragraph("------------------------------------------------"));
        document.add(new Paragraph("Total Price: ₹" + String.format("%.2f", totalPrice)));
        document.add(new Paragraph("------------------------------------------------"));
        document.add(new Paragraph("Thank you for your purchase!"));

        document.close();

        // Send Email with Attachment
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject("Your Pharmacy Bill - " + medicine.getName());
        helper.setText("Dear Customer,\n\nPlease find attached the bill for your recent purchase of "
                + medicine.getName() + ".\n\nThank you,\nPharmacy Management Team");

        helper.addAttachment("Bill_" + medicine.getName() + ".pdf", new ByteArrayResource(outputStream.toByteArray()));

        mailSender.send(message);
    }
}
