package com.scm.services;

import java.io.File;

import org.springframework.stereotype.Service;

import com.scm.entities.User;

@Service
public interface EmailService {

	void sendEmail(String to,String subject, String body);
	void sendEmailWithHtml(String to, String subject, String htmlBody);
	void sendEmailWithAttachment(String to, String subject, String body, File attachment);
	void sendVerificationEmail(User user);
	void sendContactUsEmail(String name, String email, String message);
	void sendDirectEmail(String userEmail, String subject, String body, String contactEmail);
}
