package com.compass.application.services;

import com.compass.application.services.exceptions.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendResetToken(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Password Reset Request");
            message.setText("To reset your password, click on the link: http://localhost:8080/v1/users/reset-password/confirm?token=" + token);
            message.setFrom(fromEmail);

            mailSender.send(message);
        } catch (Exception e) {
            throw new EmailException(e.getMessage());
        }
    }
}