package com.pharmacy.management.service;

import com.pharmacy.management.model.Medicine;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private Medicine medicine;

    @BeforeEach
    void setUp() {
        medicine = new Medicine();
        medicine.setName("Test Medicine");
        medicine.setDescription("Test Description");
        medicine.setPrice(100.0);
    }

    @Test
    void sendBillEmail_ShouldGeneratePdfAndSendEmail() throws MessagingException, IOException {
        MimeMessage mockMimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockMimeMessage);

        emailService.sendBillEmail("test@example.com", medicine, 2, 200.0);

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}
