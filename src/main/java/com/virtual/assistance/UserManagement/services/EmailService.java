package com.virtual.assistance.UserManagement.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true = HTML
        mailSender.send(message);
    }

    public String buildWelcomeEmail(String name, String email) {
        return "<html>" +
                "<body style='font-family: Arial, sans-serif; line-height:1.6;'>" +
                "<h2>Welcome to Virtual Assistance, " + name + "!</h2>" +
                "<p>Your account has been successfully created.</p>" +
                "<p><strong>Email:</strong> " + email + "</p>" +
                "<p>We’re excited to have you on board.</p>" +
                "<br>" +
                "<p>Best regards,<br/>Virtual Assistance Team</p>" +
                "</body>" +
                "</html>";
    }
}
