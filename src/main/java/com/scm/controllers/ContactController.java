package com.scm.controllers;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.ContactForm;
import com.scm.forms.ContactSearchForm;
import com.scm.helpers.AppConstants;
import com.scm.helpers.Helper;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.ContactService;
import com.scm.services.EmailService;
import com.scm.services.ImageService;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

	Logger logger=LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	ContactService contactService;
	
	@Autowired
	ImageService imageService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	EmailService emailService;
	
	//add contact page
	@RequestMapping("/add")
	public String addContactView(Model model) {
		ContactForm contactForm=new ContactForm();
		model.addAttribute("contactForm",contactForm);
		return "user/add_contact";
	}
	
	@PostMapping("/add")
	public String saveContact(@Valid @ModelAttribute ContactForm contactForm,
			BindingResult result, Authentication authentication, HttpSession session) {
		
		//process the form data
		System.out.println(contactForm.toString());
		
		//validate form
		if(result.hasErrors()) {
			session.setAttribute("message", Message.builder()
					.content("Please correct the following errors!")
					.type(MessageType.red)
					.build());
			return "user/add_contact";
		}
		
		
		String username=Helper.getEmailOfLoggedInUser(authentication);
		User user=userService.getUserByEmail(username);
		//form-> contact
		
		//image process
		
		Contact contact=new Contact();
		contact.setName(contactForm.getName());
		contact.setEmail(contactForm.getEmail());
		contact.setAddress(contactForm.getAddress());
		contact.setPhoneNumber(contactForm.getPhoneNumber());
		contact.setDescription(contactForm.getDescription());
		contact.setFavorite(contactForm.isFavorite());
		contact.setLinkedInLink(contactForm.getLinkedInLink());
		contact.setWebsiteLink(contactForm.getWebsiteLink());
		contact.setUser(user);
		if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
            String filename = UUID.randomUUID().toString();
            String fileURL = imageService.uplaodImage(contactForm.getContactImage(), filename);
            contact.setPicture(fileURL);
            contact.setCloudinaryImagePublicId(filename);

        }
		contactService.save(contact);
		
		session.setAttribute("message", Message.builder()
				.content("You have added a contact successfully!")
				.type(MessageType.green)
				.build());
		return "redirect:/user/contacts/add";
	}
	
	//view contacts
	@RequestMapping
	public String viewContacts(
			@RequestParam(value="page",defaultValue = "0") int page,
			@RequestParam(value="size", defaultValue = AppConstants.PAGE_SIZE+"")int size,
			@RequestParam(value="sortBy",defaultValue = "name") String sortBy,
			@RequestParam(value="direction",defaultValue = "asc") String direction,
			Model model,Authentication authentication) {
		//load all user contacts
		String username=Helper.getEmailOfLoggedInUser(authentication);
		User user = userService.getUserByEmail(username);
		Page<Contact> pageContact = contactService.getByUser(user,page,size,sortBy,direction);
		model.addAttribute("pageContact", pageContact);
		model.addAttribute("pageSize",AppConstants.PAGE_SIZE);
		model.addAttribute("contactSearchForm", new ContactSearchForm());
		return "user/contacts";
	}
	
	//search handler
	@RequestMapping("/search")
	public String searchHandler(
			@ModelAttribute ContactSearchForm contactSearchForm,
			@RequestParam(value="page",defaultValue = "0") int page,
			@RequestParam(value="size", defaultValue = AppConstants.PAGE_SIZE+"")int size,
			@RequestParam(value="sortBy",defaultValue = "name") String sortBy,
			@RequestParam(value="direction",defaultValue = "asc") String direction,
			Model model, Authentication authentication) {
		logger.info(" field {} keyword {}",contactSearchForm.getField(),contactSearchForm.getValue());
		String username=Helper.getEmailOfLoggedInUser(authentication);
		User user = userService.getUserByEmail(username);
		Page<Contact> pageContact=null;
		
		if(contactSearchForm.getField().equalsIgnoreCase("name")) {
			pageContact = contactService.searchByName(contactSearchForm.getValue(),size , page,sortBy, direction,user);
		}else if(contactSearchForm.getField().equalsIgnoreCase("email")) {
			pageContact= contactService.searchByEmail(contactSearchForm.getValue(),size , page,sortBy, direction,user);
		}else if(contactSearchForm.getField().equalsIgnoreCase("phone")) {
			pageContact= contactService.searchByPhoneNumber(contactSearchForm.getValue(),size , page,sortBy, direction,user);
		}
		model.addAttribute("contactSearchForm", contactSearchForm);
		model.addAttribute("pageContact",pageContact);
		model.addAttribute("pageSize",AppConstants.PAGE_SIZE);
		return "user/search";
	}

	//delete contact
	@RequestMapping("/delete/{id}")
	public String deleteContact(@PathVariable("id") String contactId, HttpSession session) {
		contactService.delete(contactId);
		logger.info("ContactId {} deleted!",contactId);
		session.setAttribute("message", 
				Message.builder()
				.content("Contact is Deleted Successfully!")
				.type(MessageType.green)
				.build()
				);
		return "redirect:/user/contacts";
	}
	
	//update contact form view
	@RequestMapping("/view/{id}")
	public String updateContactFormView(@PathVariable String id,Model model) {
		var contact=contactService.getContactById(id);
		ContactForm contactForm=new ContactForm();
		contactForm.setName(contact.getName());
		contactForm.setEmail(contact.getEmail());
		contactForm.setPhoneNumber(contact.getPhoneNumber());
		contactForm.setFavorite(contact.isFavorite());
		contactForm.setAddress(contact.getAddress());
		contactForm.setDescription(contact.getDescription());
		contactForm.setWebsiteLink(contact.getWebsiteLink());
		contactForm.setLinkedInLink(contact.getLinkedInLink());
		contactForm.setPicture(contact.getPicture());
		model.addAttribute("contactForm",contactForm);
		model.addAttribute("contactId", id);
		return "user/update_contact_view";
	}
	
	@PostMapping("/update/{contactId}")
	public String updateContact(@PathVariable("contactId") String contactId,
			@Valid @ModelAttribute ContactForm contactForm,
			BindingResult result,
			Model model) {
		if(result.hasErrors()) {
			return "user/update_contact_view";
		}
		var con=contactService.getContactById(contactId);
		con.setContactId(contactId);
		con.setEmail(contactForm.getEmail());
		con.setAddress(contactForm.getAddress());
		con.setName(contactForm.getName());
		con.setDescription(contactForm.getDescription());
		con.setFavorite(contactForm.isFavorite());
		con.setPhoneNumber(contactForm.getPhoneNumber());
		con.setWebsiteLink(contactForm.getWebsiteLink());
		con.setLinkedInLink(contactForm.getLinkedInLink());
		//process image
		if(contactForm.getContactImage()!=null && !contactForm.getContactImage().isEmpty()) {
			logger.info("File is not empty");
			String fileName=UUID.randomUUID().toString();
			String imageUrl=imageService.uplaodImage(contactForm.getContactImage(), fileName);
			con.setCloudinaryImagePublicId(fileName);
			con.setPicture(imageUrl);
			contactForm.setPicture(imageUrl);
		}else {
			logger.info("File is empty");
		}
		var updatedContact=contactService.update(con);
		logger.info("Update contactId {}",updatedContact);
		model.addAttribute("message", Message.builder().content("Contact Updated!").type(MessageType.green).build());
		return "redirect:/user/contacts/view/"+contactId;
	}
	
	// Controller method to send a direct message
	@RequestMapping("/sendDirectEmail/{contactId}")
	public String sendMessage(@PathVariable("contactId") String contactId,
	                           @RequestParam("message") String message,
	                           Authentication authentication, 
	                           HttpSession session) {
	    // Get logged-in user
	    String username = Helper.getEmailOfLoggedInUser(authentication);
	    User loggedInUser = userService.getUserByEmail(username);

	    // Retrieve the contact to whom the message will be sent
	    Contact contact = contactService.getContactById(contactId);

	    if (contact != null) {
	        try {
	            // Send message to the contact's email (subject can be optional or dynamically generated)
	            String subject = "Direct Message from " + loggedInUser.getName() +"by SCM"; // Customize subject if needed
	            emailService.sendDirectEmail(username, subject, message, contact.getEmail());
	            session.setAttribute("message", Message.builder()
	                    .content("Message sent successfully!")
	                    .type(MessageType.green)
	                    .build());
	        } catch (Exception e) {
	            session.setAttribute("message", Message.builder()
	                    .content("Failed to send message. Please try again.")
	                    .type(MessageType.red)
	                    .build());
	        }
	    } else {
	        session.setAttribute("message", Message.builder()
	                .content("Contact not found!")
	                .type(MessageType.red)
	                .build());
	    }
	    return "redirect:/user/contacts";
	}

	@PostMapping("/sendDirectEmail")
	public String sendDirectEmail(@RequestParam("emailBody") String emailBody,
	                               @RequestParam("subject") String subject,
	                               @RequestParam("contactEmail") String contactEmail,
	                               Authentication authentication) {
	    String userEmail = Helper.getEmailOfLoggedInUser(authentication);
	    emailService.sendDirectEmail(userEmail, subject, emailBody, contactEmail);
	    return "redirect:/user/contacts"; 
	}

}
