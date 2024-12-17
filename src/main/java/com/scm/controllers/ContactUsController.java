package com.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.scm.forms.FeedbackForm;
import com.scm.services.EmailService;

import jakarta.validation.Valid;

@Controller
public class ContactUsController {

	@Autowired
	EmailService emailService;
	
	 @GetMapping("/contact")
	    public String showContactForm(@ModelAttribute FeedbackForm feedbackForm) {
	        return "contact";  // Return contact form page
	    }
	
	 @PostMapping("/contact")
	    public String sendContactEmail(@Valid @ModelAttribute  FeedbackForm feedbackForm, 
	                                   BindingResult result) {
	        if (result.hasErrors()) {
	            // Return the contact form page with validation errors
	            return "contact";
	        }

	        // Send email if no validation errors
	        String subject = "New Message from " + feedbackForm.getName();
	        String body = "Name: " + feedbackForm.getName() + "\nEmail: " + feedbackForm.getEmail() + "\n\nMessage:\n" + feedbackForm.getMessage();

	        emailService.sendContactUsEmail(feedbackForm.getEmail(), "User Feedback", body);

	        return "thankyou";  // Return thank you page
	    }
}
