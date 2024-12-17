package com.scm.services.impl;

import java.io.File;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scm.entities.User;
import com.scm.repositories.UserRepo;
import com.scm.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    JavaMailSender emailSender;
    
    @Autowired
    UserRepo userRepo;
    
    @Value("${spring.mail.username}")
    String domainName;

    @Override
    public void sendEmail(String to, String subject, String body) {
    	logger.info("Preparing to send email to {}", to);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(domainName);
        try {
            emailSender.send(message);
            logger.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void sendEmailWithHtml(String to, String subject, String htmlBody) {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true indicates HTML
            helper.setFrom(domainName);
            emailSender.send(mimeMessage);
            logger.info("HTML email sent successfully to {}", to);
        } catch (MessagingException e) {
            logger.error("Failed to send HTML email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send HTML email", e);
        }
    }

    @Override
    public void sendEmailWithAttachment(String to, String subject, String body, File attachment) {
        if (attachment == null || !attachment.exists()) {
            throw new IllegalArgumentException("Attachment file is invalid or does not exist");
        }
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);
            helper.setFrom(domainName);
            helper.addAttachment(attachment.getName(), attachment);
            emailSender.send(mimeMessage);
            logger.info("Email with attachment sent successfully to {}", to);
        } catch (MessagingException e) {
            logger.error("Failed to send email with attachment to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email with attachment", e);
        }
    }
    
    @Override
    public void sendVerificationEmail(User user) {
        String token = UUID.randomUUID().toString();  // Generate a unique token
        
        user.setEmailToken(token);
        userRepo.save(user);
        String verificationUrl = "http://localhost:8081/auth/verify-email?token=" + token;

        String subject = "Email Verification";
        String body = "Click on the following link to verify your email: " + verificationUrl;

        try {
            sendEmailWithHtml(user.getEmail(), subject, body);  // Send the verification email
            logger.info("Verification email sent successfully to {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send verification email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    public void sendContactUsEmail(String userEmail, String subject, String body) {
        logger.info("Preparing to send email to smartcontactmananger@gmail.com from {}", userEmail);
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("smartcontactmananger@gmail.com");
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(userEmail);

        try {
            emailSender.send(message);
            logger.info("Email sent successfully from {} to smartcontactmananger@gmail.com", userEmail);
        } catch (Exception e) {
            logger.error("Failed to send email from {}: {}", userEmail, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
   
    }
    
    public void sendDirectEmail(String userEmail, String subject, String body, String contactEmail) {
        logger.info("Preparing to send email to contact from {}", userEmail);
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(contactEmail); // The contact's email will be here
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(userEmail);

        try {
            emailSender.send(message);
            logger.info("Email sent successfully from {} to contact {}", userEmail, contactEmail);
        } catch (Exception e) {
            logger.error("Failed to send email from {} to {}: {}", userEmail, contactEmail, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    
}
